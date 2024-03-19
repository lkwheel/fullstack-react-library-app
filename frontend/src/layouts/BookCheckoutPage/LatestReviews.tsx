import { Link } from 'react-router-dom';
import ReviewModel from '../../model/ReviewModel'
import { Review } from '../Utils/Review';

export const LatestReviews: React.FC<{
    reviews: ReviewModel[],
    bookId: string | undefined,
    mobile: boolean
}> = (props) => {
    return (
        <div className={props.mobile ? 'mt-3' : 'row mt-3'}>
            <div className={props.mobile ? '' : 'col-sm-2 col-md-2'}>
                <h2>Latest Reviews</h2>
            </div>
            <div className='col-sm-10 col-md-10'>
                {props.reviews.length > 0 ?
                    <>
                        {props.reviews.slice(0, 3).map((eachReview) => (
                            <Review review={eachReview} key={eachReview.id}></Review>
                        ))}

                        <div className='m3'>
                            <Link type='button'
                                className='btn main-color btn-md text-white'
                                to={`/reviewList/${props.bookId}`}>
                                Show all reviews
                            </Link>
                        </div>
                    </>
                    :
                    <div className='m3'>
                        <p className='lead'>There are no reviews for this book</p>
                    </div>
                }
            </div>
        </div>
    );
};