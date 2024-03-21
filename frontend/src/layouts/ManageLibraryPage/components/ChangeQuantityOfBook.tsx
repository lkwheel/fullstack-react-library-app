import { useEffect, useState } from 'react';
import BookModel from '../../../model/BookModel';
import { useAuth0 } from '@auth0/auth0-react';

export const ChangeQuantityOfBook: React.FC<{ book: BookModel, deleteBook: any }> = (props, key) => {
    const { user, isAuthenticated, getAccessTokenSilently } = useAuth0();

    const [quantity, setQuantity] = useState<number>(0);
    const [remaining, setRemaining] = useState<number>(0);

    useEffect(() => {
        const fetchBookInState = () => {
            props.book.copies ? setQuantity(props.book.copies) : setQuantity(0);
            props.book.copiesAvailable ? setRemaining(props.book.copiesAvailable) : setRemaining(0);
        };
        fetchBookInState();
    }, [props]);

    async function increaseQuantity() {
        if (isAuthenticated && user?.email) {
            const apiAccessToken = await getAccessTokenSilently();
            const url = `http://localhost:6060/api/admin/protected/increase/book/quantity?bookId=${props.book?.id}`;

            const requestOptions = {
                method: 'PUT',
                headers: {
                    'content-type': 'application/json',
                    'Authorization': `Bearer ${apiAccessToken}`,
                },
            };
            const response = await fetch(url, requestOptions);
            if (!response.ok) {
                throw new Error('Something went wrong increasing book quantity');
            }

            setQuantity(quantity + 1);
            setRemaining(remaining + 1);
        }
    }

    async function decreaseQuantity() {
        if (isAuthenticated && user?.email) {
            const apiAccessToken = await getAccessTokenSilently();
            const url = `http://localhost:6060/api/admin/protected/decrease/book/quantity?bookId=${props.book?.id}`;

            const requestOptions = {
                method: 'PUT',
                headers: {
                    'content-type': 'application/json',
                    'Authorization': `Bearer ${apiAccessToken}`,
                },
            };
            const response = await fetch(url, requestOptions);
            if (!response.ok) {
                throw new Error('Something went wrong decreasing book quantity');
            }

            setQuantity(quantity - 1);
            setRemaining(remaining - 1);
        }
    }

    async function deleteBook() {
        if (isAuthenticated && user?.email) {
            const apiAccessToken = await getAccessTokenSilently();
            const url = `http://localhost:6060/api/admin/protected/delete/book?bookId=${props.book?.id}`;

            const requestOptions = {
                method: 'DELETE',
                headers: {
                    'content-type': 'application/json',
                    'Authorization': `Bearer ${apiAccessToken}`,
                },
            };
            const response = await fetch(url, requestOptions);
            if (!response.ok) {
                throw new Error('Something went wrong deleting book');
            }

            props.deleteBook();
        }
    }

    return (
        <div key={props.book.id} className='card mt-3 shadow p-3 mb-3 bg-body rounded'>
            <div className='row g-0'>
                <div className='col-md-2'>
                    <div className='d-none d-lg-block'>
                        {props.book.img ?
                            <img src={props.book.img}
                                width='123'
                                height='196'
                                alt='book' />
                            :
                            <img src={require('../../../Images/BooksImages/book-luv2code-1000.png')}
                                width='123'
                                height='196'
                                alt='book' />
                        }
                    </div>
                    <div className='d-lg-none d-flex justify-content-center align-items-center'>
                        {props.book.img ?
                            <img src={props.book.img}
                                width='123'
                                height='196'
                                alt='Book' />
                            :
                            <img src={require('../../../Images/BooksImages/book-luv2code-1000.png')}
                                width='123'
                                height='196'
                                alt='Book' />

                        }
                    </div>
                </div>
                <div className='col-md-6'>
                    <div className='card-body'>
                        <h5 className='card-title'>{props.book.author}</h5>
                        <h4>{props.book.title}</h4>
                        <p className='card-text'>{props.book.description}</p>
                    </div>
                </div>
                <div className='mt-3 col-md-4'>
                    <div className='d-flex justify-content-center align-items-center'>
                        <p>Total Quantity: <b>{quantity}</b></p>
                    </div>
                    <div className='d-flex justify-content-center align-items-center'>
                        <p>Books Remaining: <b>{remaining}</b></p>
                    </div>
                </div>
                <div className='mt-3 mb-1 col-md-1'>
                    <div className='d-flex justify-content-start'>
                        <button className='m1 btn btn-md btn-danger'
                            onClick={deleteBook}>Delete</button>
                    </div>
                </div>
                <button className='m1 btn btn-md main-color text-white'
                    onClick={increaseQuantity}>Add Quantity</button>
                <button className='m1 btn btn-md btn-warning'
                    onClick={decreaseQuantity}>Decrease Quantity</button>
            </div>
        </div>
    );
}