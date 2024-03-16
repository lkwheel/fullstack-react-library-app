import { useAuth0 } from '@auth0/auth0-react';
import { useEffect, useState } from 'react';
import { Redirect } from 'react-router-dom';
import AuthUtils from '../../Auth/AuthUtils';
import { SpinnerLoading } from '../Utils/SpinnerLoading';
import { AdminMessages } from './components/AdminMessages';

export const ManageLibraryPage = () => {
    const { user, isAuthenticated, getAccessTokenSilently } = useAuth0();

    const [changeQuantityOfBooksClick, setChangeQuantityOfBookClick] = useState(false);
    const [messagesClick, setMessagesClick] = useState(false);

    const [isAdmin, setIsAdmin] = useState(false);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [httpError, setHttpError] = useState(null);

    useEffect(() => {
        const fetchCheckAdminUseRole = async () => {
            if (isAuthenticated && user?.email) {
                try {
                    const apiAccessToken = await getAccessTokenSilently();
                    const url = `http://localhost:6060/api/user/protected/permissions`;
                    const requestOptions = {
                        method: 'GET',
                        headers: {
                            'content-type': 'application/json',
                            'Authorization': `Bearer ${apiAccessToken}`,
                        }
                    };
                    const response = await fetch(url, requestOptions);
                    if (!response.ok) {
                        throw new Error('Something went wrong getting user permissions');
                    }

                    const permissions = await response.json();
                    // Check if 'admin' is included in the permissions array
                    const isAdmin = permissions.includes('admin');
                    setIsAdmin(isAdmin);
                    setIsLoading(false);
                } catch (error: any) {
                    setIsAdmin(false);
                    setHttpError(error.message);
                    setIsLoading(false);
                }
            } else {
                setIsAdmin(false);
                setIsLoading(false);
            }
        };

        fetchCheckAdminUseRole();
    }, [getAccessTokenSilently, isAuthenticated, user]);


    function addBookClickFunction() {
        setChangeQuantityOfBookClick(false);
        setMessagesClick(false);
    }

    function changeQuantityOfBooksClickFunction() {
        setChangeQuantityOfBookClick(true);
        setMessagesClick(false);
    }

    function messagesClickFunction() {
        setChangeQuantityOfBookClick(false);
        setMessagesClick(true);
    }

    if (!isAuthenticated || isLoading) {
        return (
            <SpinnerLoading />
        );
    }

    if (!isAdmin) {
        // Render a redirect if the user is not authenticated or not an admin
        return <Redirect to='/home' />;
    }

    if (httpError) {
        return (
            <div className='container m-5'>
                <p>{httpError}</p>
            </div>
        );
    }

    return (
        <div className='container'>
            <div className='mt-5'>
                <h3>Manage Library</h3>
                <nav>
                    <div className='nav nav-tabs'
                        id='nav-tab'
                        role='tablist'>
                        <button className='nav-link active'
                            id='nav-add-book-tab'
                            onClick={addBookClickFunction}
                            data-bs-toggle='tab'
                            data-bs-target='#nav-add-book'
                            type='button'
                            role='tab'
                            aria-controls='nav-add-book'
                            aria-selected='false'>
                            Add new book
                        </button>
                        <button className='nav-link'
                            id='nav-quantity-tab'
                            onClick={changeQuantityOfBooksClickFunction}
                            data-bs-toggle='tab'
                            data-bs-target='#nav-quantity'
                            type='button'
                            role='tab'
                            aria-controls='nav-quantity'
                            aria-selected='true'>
                            Change quantity
                        </button>
                        <button className='nav-link'
                            id='nav-messages-tab'
                            onClick={messagesClickFunction}
                            data-bs-toggle='tab'
                            data-bs-target='#nav-messages'
                            type='button'
                            role='tab'
                            aria-controls='nav-messages'
                            aria-selected='true'>
                            Messages
                        </button>
                    </div>
                </nav>
                <div className='tab-content'
                    id='nav-tabContent'>
                    <div className='tab-pane fade show active'
                        id='nav-add-book'
                        role='tabpanel'
                        aria-labelledby='nav-add-book-tab'>
                        Add new book
                    </div>
                    <div className='tab-pane fade'
                        id='nav-quantity'
                        role='tabpanel'
                        aria-labelledby='nav-quantity-tab'>
                        {changeQuantityOfBooksClick ? <>Change Quantity</> : <></>}
                    </div>
                    <div className='tab-pane fade'
                        id='nav-messages'
                        role='tabpanel'
                        aria-labelledby='nav-messages-tab'>
                        {messagesClick ? <AdminMessages /> : <></>}
                    </div>
                </div>
            </div>
        </div>
    );
}