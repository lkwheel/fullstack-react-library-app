import { useEffect, useState } from 'react';
import { useAuth0 } from '@auth0/auth0-react';
import BookModel from '../../model/BookModel';
import { SpinnerLoading } from '../Utils/SpinnerLoading';
import { StarsReview } from '../Utils/StarsReview';
import { CheckAndReviewBox } from './CheckAndReviewBox';
import ReviewModel from '../../model/ReviewModel';
import { LatestReviews } from './LatestReviews';
import ReviewRequestModel from '../../model/ReviewRequestModel';

export const BookCheckoutPage = () => {
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

    const { user, isAuthenticated, getAccessTokenSilently } = useAuth0();

    const bookId = (window.location.pathname).split('/')[2];

    useEffect(() => {
        const fetchBook = async () => {
            const baseUrl = `http://localhost:6060/api/books`;
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
    }, [isCheckedOut]);

    useEffect(() => {
        const fetchBookReviews = async () => {
            const reviewUrl = `http://localhost:6060/api/reviews/find-by-book-id?bookId=${bookId}`;
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
    }, [reviews]);

    useEffect(() => {
        const fetchUserReviews = async () => {
            if (isAuthenticated && user?.email) {
                const apiAccessToken = await getAccessTokenSilently();
                const url = `http://localhost:6060/api/reviews/protected/user/book?userEmail=${user?.email}&bookId=${bookId}`;
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
    }, [isReviewLeft, getAccessTokenSilently]);

    useEffect(() => {
        const fetchUserCurrentLoansCount = async () => {
            if (isAuthenticated && user?.email) {
                const apiAccessToken = await getAccessTokenSilently();
                const url = `http://localhost:6060/api/books/protected/current-loans/count?userEmail=${user?.email}`;
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
    }, [getAccessTokenSilently, isCheckedOut]);

    useEffect(() => {
        const fetchUserCheckedOutBook = async () => {
            if (isAuthenticated && user?.email) {
                const apiAccessToken = await getAccessTokenSilently();
                const url = `http://localhost:6060/api/books/protected/is-checked-out/by-user?bookId=${bookId}&userEmail=${user?.email}`;
                const requestOptions = {
                    method: 'GET',
                    headers: {
                        'content-type': 'application/json',
                        'Authorization': `Bearer ${apiAccessToken}`,
                    }
                };
                const bookCheckedOutResponse = await fetch(url, requestOptions);
                if (!bookCheckedOutResponse.ok) {
                    throw new Error('Something went wrong getting book checked out');
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
    }, [getAccessTokenSilently]);

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
        const url = `http://localhost:6060/api/books/protected/checkout?bookId=${book?.id}&userEmail=${user?.email}`;
        const requestOptions = {
            method: 'PUT',
            headers: {
                'content-type': 'application/json',
                'Authorization': `Bearer ${apiAccessToken}`,
            }
        };

        const checkoutResponse = await fetch(url, requestOptions);
        if (!checkoutResponse.ok) {
            throw new Error('Something went wrong - checkout book');
        }
        setIsCheckedOut(true);
    }

    async function submitReview(starInput: number, reviewDescription: string) {

        let bookId: number = 0;
        if (book?.id) {
            bookId = book.id;
        }

        const apiAccessToken = await getAccessTokenSilently();
        const reviewRequestModel = new ReviewRequestModel(starInput, bookId, reviewDescription);
        const url = `http://localhost:6060/api/reviews/protected?userEmail=${user?.email}`;
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