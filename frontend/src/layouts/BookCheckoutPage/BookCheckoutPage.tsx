import { useAuth0 } from '@auth0/auth0-react';
import { useEffect, useRef, useState } from 'react';
import { useParams } from 'react-router-dom';
import BookModel from '../../models/BookModel';
import ReviewModel from '../../models/ReviewModel';
import ReviewRequestModel from '../../models/ReviewRequestModel';
import { SpinnerLoading } from '../Utils/SpinnerLoading';
import { StarsReview } from '../Utils/StarsReview';
import { CheckAndReviewBox } from './CheckAndReviewBox';
import { LatestReviews } from './LatestReviews';

interface Params {
    bookId: string;
}

export const BookCheckoutPage = () => {
    const apiUrl = process.env.REACT_APP_API_URL;
    const [book, setBook] = useState<BookModel>();
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [httpError, setHttpError] = useState(null);

    // Review state
    const [reviews, setReviews] = useState<ReviewModel[]>([]);
    const [totalStars, setTotalStars] = useState(0);
    const [isLoadingReview, setIsLoadingReview] = useState(true);

    const [isReviewLeft, setIsReviewLeft] = useState(false);
    const [isLoadingUserReview, setIsLoadingUserReview] = useState(true);

    // Loans Count state
    const [currentLoansCount, setCurrentLoansCount] = useState(0);
    const [isLoadingCurrentStateCount, setIsLoadingCurrentStateCount] = useState(true);

    // Is Book Checked Out?
    const [isCheckedOut, setIsCheckedOut] = useState(false);
    const [isLoadingBookCheckedOut, setIsLoadingBookCheckedOut] = useState(true);

    const [displayError, setDisplayError] = useState(false);

    const { user, isAuthenticated, getAccessTokenSilently } = useAuth0();

    // Handle bookIds
    const { bookId } = useParams<Params>();
    const prevBookIdRef = useRef<string | null>(null);

    useEffect(() => {
        const fetchBook = async () => {
            const baseUrl = `${apiUrl}/books`;
            let url = `${baseUrl}`;

            if (bookId) {
                url = `${baseUrl}/${bookId}`; // Add bookId if present
            } else {
                // Add pagination parameters only for fetching all books
                // Hard coded to first pages since we can only check out 5 at this time
                url = `${url}?page=0&size=9`;
            }
            const response = await fetch(url);
            if (!response.ok) {
                throw new Error('Something went wrong fetching books');
            }
            const responseJson = await response.json();
            const loadedBook: BookModel = {
                id: responseJson.id,
                title: responseJson.title,
                author: responseJson.author,
                description: responseJson.description,
                copies: responseJson.copies,
                copiesAvailable: responseJson.copiesAvailable,
                img: responseJson.img,
            };
            setBook(loadedBook);
            setIsLoading(false);
        };
        fetchBook().catch((error: any) => {
            setIsLoading(false);
            setHttpError(error.message);
        })
        window.scrollTo(0, 0);
    }, [apiUrl, bookId, isCheckedOut]);

    useEffect(() => {
        const fetchBookReviews = async () => {
            if (prevBookIdRef.current === bookId) {
                return; // Skip fetching if bookId hasn't changed
            }

            prevBookIdRef.current = bookId; // Update previous bookId
            const reviewUrl = `${apiUrl}/reviews/find-by-book-id?bookId=${bookId}`;
            const responseReviews = await fetch(reviewUrl);

            if (!responseReviews.ok) {
                throw new Error('Something went wrong getting book reviews');
            }
            const responseJsonReviews = await responseReviews.json();
            const responseData = responseJsonReviews.content;

            const loadedReviews: ReviewModel[] = [];

            let weightedStarReview = 0;

            for (const key in responseData) {
                loadedReviews.push({
                    id: responseData[key].id,
                    userEmail: responseData[key].userEmail,
                    date: responseData[key].date,
                    rating: responseData[key].rating,
                    bookId: responseData[key].bookId,
                    reviewDescription: responseData[key].reviewDescription
                });
                weightedStarReview = weightedStarReview + responseData[key].rating;
            }

            if (loadedReviews) {
                const round = (Math.round((weightedStarReview / loadedReviews.length) * 2) / 2).toFixed(1);
                setTotalStars(Number(round));
            }

            setReviews(loadedReviews);
            setIsLoadingReview(false);
        };

        fetchBookReviews().catch((error: any) => {
            setIsLoadingReview(false);
            setHttpError(error.message);
        })
    }, [apiUrl, bookId, reviews]);

    useEffect(() => {
        const fetchUserReviews = async () => {
            if (isAuthenticated && user?.email) {
                const apiAccessToken = await getAccessTokenSilently();
                const url = `${apiUrl}/reviews/protected/user/book?bookId=${bookId}`;
                const requestOptions = {
                    method: 'GET',
                    headers: {
                        'content-type': 'application/json',
                        'Authorization': `Bearer ${apiAccessToken}`,
                    }
                };

                const userReviewResponse = await fetch(url, requestOptions);
                if (!userReviewResponse.ok) {
                    throw new Error('Something went wrong getting user reviews');
                }

                const userReviewResponseJson = await userReviewResponse.json();
                setIsReviewLeft(userReviewResponseJson);
            }
            setIsLoadingUserReview(false);
        }

        fetchUserReviews().catch((error: any) => {
            setIsLoadingUserReview(false);
            setHttpError(error.message);
        });
    }, [apiUrl, bookId, isAuthenticated, isReviewLeft, getAccessTokenSilently, user]);

    useEffect(() => {
        const fetchUserCurrentLoansCount = async () => {
            if (isAuthenticated && user?.email) {
                const apiAccessToken = await getAccessTokenSilently();
                const url = `${apiUrl}/books/protected/current-loans/count`;
                const requestOptions = {
                    method: 'GET',
                    headers: {
                        'content-type': 'application/json',
                        'Authorization': `Bearer ${apiAccessToken}`,
                    }
                };

                const currentLoansCountResponse = await fetch(url, requestOptions);
                if (!currentLoansCountResponse.ok) {
                    throw new Error('Something went wrong getting current loans count');
                }

                const currentLoansResponseJson = await currentLoansCountResponse.json();
                setCurrentLoansCount(currentLoansResponseJson);
            }
            setIsLoadingCurrentStateCount(false);
        };

        fetchUserCurrentLoansCount().catch((error: any) => {
            setIsLoadingCurrentStateCount(false);
            setHttpError(error.message);
        });
    }, [apiUrl, getAccessTokenSilently, isAuthenticated, isCheckedOut, user]);

    useEffect(() => {
        const fetchUserCheckedOutBook = async () => {
            if (isAuthenticated && user?.email) {
                const apiAccessToken = await getAccessTokenSilently();
                const url = `${apiUrl}/books/protected/is-checked-out/by-user?bookId=${bookId}`;
                const requestOptions = {
                    method: 'GET',
                    headers: {
                        'content-type': 'application/json',
                        'Authorization': `Bearer ${apiAccessToken}`,
                    }
                };
                const bookCheckedOutResponse = await fetch(url, requestOptions);
                if (!bookCheckedOutResponse.ok) {
                    throw new Error('Something went wrong checking out book');
                }

                const bookCheckedOutResponseJson = await bookCheckedOutResponse.json();
                setIsCheckedOut(bookCheckedOutResponseJson);
            }
            setIsLoadingBookCheckedOut(false);
        };

        fetchUserCheckedOutBook().catch((error: any) => {
            setIsLoadingBookCheckedOut(false);
            setHttpError(error.message);
        });
    }, [apiUrl, bookId, displayError, getAccessTokenSilently, isAuthenticated, user]);

    if (isLoading ||
        isLoadingReview ||
        isLoadingCurrentStateCount ||
        isLoadingBookCheckedOut ||
        isLoadingUserReview) {
        return (
            <SpinnerLoading />
        );
    }

    if (httpError) {
        return (
            <div className='container m-5'>
                <p>{httpError}</p>
            </div>
        );
    }

    async function checkoutBook() {
        const apiAccessToken = await getAccessTokenSilently();
        const url = `${apiUrl}/books/protected/checkout?bookId=${book?.id}`;
        const requestOptions = {
            method: 'PUT',
            headers: {
                'content-type': 'application/json',
                'Authorization': `Bearer ${apiAccessToken}`,
            }
        };

        const checkoutResponse = await fetch(url, requestOptions);
        if (!checkoutResponse.ok) {
            setDisplayError(true);
        } else {
            setDisplayError(false)
            setIsCheckedOut(true);
        }
    }

    async function submitReview(starInput: number, reviewDescription: string) {

        let bookId: string = '';
        if (book?.id) {
            bookId = book.id;
        }

        const apiAccessToken = await getAccessTokenSilently();
        const reviewRequestModel = new ReviewRequestModel(starInput, bookId, reviewDescription);
        const url = `${apiUrl}/reviews/protected`;
        const requestOptions = {
            method: 'POST',
            headers: {
                'content-type': 'application/json',
                'Authorization': `Bearer ${apiAccessToken}`,
            },
            body: JSON.stringify(reviewRequestModel)
        };
        const returnResponse = await fetch(url, requestOptions);
        if (!returnResponse.ok) {
            throw new Error('Something went wrong - submit review');
        }
        setIsReviewLeft(true);
    }

    return (
        <div>
            <div className='container mb-5 d-none d-lg-block'>
                {displayError && <div className='alert alert-danger mt-3' role='alert'>
                    Please pay outstanding fees and/or return late book(s).
                </div>
                }
                <div className='row mt-5'>
                    <div className='col-sm-2 col-md-2'>
                        {book?.img ?
                            <img src={book?.img} width='226' height='349' alt='Book' />
                            :
                            <img src={require('../../Images/BooksImages/book-luv2code-1000.png')}
                                width='226' height='349' alt='Book' />
                        }
                    </div>
                    <div className='col-4 md-4 container'>
                        <div className='ml-2'>
                            <h2>{book?.title}</h2>
                            <h5 className='text-primary'>{book?.author}</h5>
                            <p className='lead'>{book?.description}</p>
                            <StarsReview rating={totalStars} size={32} />
                        </div>
                    </div>
                    <CheckAndReviewBox
                        book={book}
                        mobile={false}
                        currentLoansCount={currentLoansCount}
                        isAuthenticated={isAuthenticated}
                        isCheckedOut={isCheckedOut}
                        checkoutBook={checkoutBook}
                        isReviewLeft={isReviewLeft}
                        submitReview={submitReview} />
                </div>
                <hr />
                <LatestReviews reviews={reviews} bookId={book?.id} mobile={false} />
            </div>
            <div className='container d-lg-none mt-5 mb-5'>
                {displayError && <div className='alert alert-danger mt-3' role='alert'>
                    Please pay outstanding fees and/or return late book(s).
                </div>
                }
                <div className='d-flex justify-content-center align-items-center'>
                    {book?.img ?
                        <img src={book?.img} width='226' height='349' alt='Book' />
                        :
                        <img src={require('../../Images/BooksImages/book-luv2code-1000.png')}
                            width='226' height='349' alt='Book' />
                    }
                </div>
                <div className='mt-4'>
                    <div className='ml-2'>
                        <h2>{book?.title}</h2>
                        <h5 className='text-primary'>{book?.author}</h5>
                        <p className='lead'>{book?.description}</p>
                        <StarsReview rating={totalStars} size={32} />
                    </div>
                </div>
                <CheckAndReviewBox
                    book={book}
                    mobile={true}
                    currentLoansCount={currentLoansCount}
                    isAuthenticated={isAuthenticated}
                    isCheckedOut={isCheckedOut}
                    checkoutBook={checkoutBook}
                    isReviewLeft={isReviewLeft}
                    submitReview={submitReview} />
                <hr />
                <LatestReviews reviews={reviews} bookId={book?.id} mobile={true} />
            </div>
        </div>
    );
};