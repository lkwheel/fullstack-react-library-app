import { useAuth0 } from '@auth0/auth0-react';
import { useEffect, useState } from 'react';
import { SpinnerLoading } from '../Utils/SpinnerLoading';
import { CardElement, useElements, useStripe } from '@stripe/react-stripe-js';
import { Link } from 'react-router-dom';
import PaymentInfoRequest from '../../models/PaymentInfoRequest';

export const PaymentPage = () => {
    const apiUrl = process.env.REACT_APP_API_URL;

    const { user, isAuthenticated, getAccessTokenSilently } = useAuth0();
    const [httpError, setHttpError] = useState(false);
    const [submitDisabled, setSubmitDisabled] = useState(false);
    const [fees, setFees] = useState(0);
    const [loadingFees, setLoadingFees] = useState(false);

    useEffect(() => {
        const fetchFees = async () => {
            if (isAuthenticated && user?.email) {
                const apiAccessToken = await getAccessTokenSilently();
                const url = `${apiUrl}/payment/protected`;
                const requestOptions = {
                    method: 'GET',
                    headers: {
                        'content-type': 'application/json',
                        'Authorization': `Bearer ${apiAccessToken}`,
                    }
                };

                const paymentResponse = await fetch(url, requestOptions);
                if (!paymentResponse.ok) {
                    throw new Error('Something went wrong getting user fees');
                }

                const paymentResponseJson = await paymentResponse.json();
                setFees(paymentResponseJson.amount);
                setLoadingFees(false);
            }
        };
        fetchFees().catch((error: any) => {
            setLoadingFees(false);
            setHttpError(true);
        });
    }, [apiUrl, getAccessTokenSilently, isAuthenticated, user]);

    const elements = useElements();
    const stripe = useStripe();

    async function checkout() {
        if (!stripe || !elements || !elements.getElement(CardElement)) {
            return;
        }

        setSubmitDisabled(true);

        let paymentInfo = new PaymentInfoRequest(Math.round(fees * 100), 'USD', user?.email);
        const apiAccessToken = await getAccessTokenSilently();
        const paymentIntentUrl = `${apiUrl}/payment/protected/payment-intent`;
        const requestOptions = {
            method: 'POST',
            headers: {
                'content-type': 'application/json',
                'Authorization': `Bearer ${apiAccessToken}`,
            },
            body: JSON.stringify(paymentInfo)
        };

        const stripeResponse = await fetch(paymentIntentUrl, requestOptions);
        if (!stripeResponse.ok) {
            setHttpError(true);
            setSubmitDisabled(true);
            throw new Error('Something went wrong getting user payment');
        }

        const stripeResponseJson = await stripeResponse.json();

        stripe.confirmCardPayment(
            stripeResponseJson.client_secret, {
            payment_method: {
                card: elements.getElement(CardElement)!,
                billing_details: {
                    email: user?.email
                }
            }
        }, { handleActions: false }
        ).then(async function (result: any) {
            if (result.error) {
                setSubmitDisabled(false);
                alert('There was an error');
            } else {
                const paymentCompleteUrl = `${apiUrl}/payment/protected/payment-complete`;
                const requestOptions = {
                    method: 'PUT',
                    headers: {
                        'content-type': 'application/json',
                        'Authorization': `Bearer ${apiAccessToken}`,
                    },
                };
                const stripeResponse = await fetch(paymentCompleteUrl, requestOptions);
                if (!stripeResponse.ok) {
                    setHttpError(true);
                    setSubmitDisabled(false);
                    throw new Error('Something went wrong completing payment');
                }
                setFees(0);
                setHttpError(false);
                setLoadingFees(false);
            }
        }).catch(() => {
            setHttpError(true);
            setLoadingFees(false);
            throw new Error('Something went wrong completing payment');
        });
    }

    if (loadingFees) {
        <SpinnerLoading />
    }

    if (httpError) {
        return (
            <div className='container m-5'>
                <p>Something went wrong!</p>
            </div>
        )
    }

    return (
        <div className='container'>
            {fees > 0 && <div className='card mt-3'>
                <h5 className='card-header'>Fees pending: <span className='text-danger'>${fees}</span></h5>
                <div className='card-body'>
                    <h5 className='card-title mb-3'>Credit Card</h5>
                    <CardElement id='card-element' />
                    <button disabled={submitDisabled} type='button' className='btn btn-md main-color text-white mt-3'
                        onClick={checkout}>
                        Pay fees
                    </button>
                </div>
            </div>}

            {fees === 0 &&
                <div className='mt-3'>
                    <h5>You have no fees!</h5>
                    <Link type='button' className='btn main-color text-white' to='search'>
                        Explore top books
                    </Link>
                </div>
            }

            {submitDisabled &&
                <SpinnerLoading />
            }
        </div>
    );
};