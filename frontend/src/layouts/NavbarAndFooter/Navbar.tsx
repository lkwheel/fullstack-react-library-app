import { NavLink } from 'react-router-dom';
import { LoginButton } from '../../Auth/LoginButton';
import { useAuth0 } from '@auth0/auth0-react';
import { LogoutButton } from '../../Auth/LogoutButton';
import { useEffect, useState } from 'react';
import { SpinnerLoading } from '../Utils/SpinnerLoading';

export const Navbar = () => {

    const apiUrl = process.env.REACT_APP_API_URL;
    const { user, isAuthenticated, getAccessTokenSilently } = useAuth0();

    const [isAdmin, setIsAdmin] = useState(false);
    const [isLoading, setIsLoading] = useState<boolean>(true);

    useEffect(() => {
        const fetchCheckAdminUseRole = async () => {
            if (isAuthenticated && user?.email) {
                try {
                    const apiAccessToken = await getAccessTokenSilently();
                    const url = `${apiUrl}/user/protected/permissions`;
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
                    setIsAdmin(true);
                    setIsLoading(false);
                } catch (error: any) {
                    setIsAdmin(false);
                    setIsLoading(false);
                }
            } else {
                setIsAdmin(false);
                setIsLoading(false);
            }
        };

        fetchCheckAdminUseRole();
    }, [apiUrl, getAccessTokenSilently, isAuthenticated, user, isAdmin]);

    if (isLoading) {
        return (
            <SpinnerLoading />
        );
    }

    return (
        <nav className='navbar navbar-expand-lg navbar-dark main-color py-3'>
            <div className='container-fluid'>
                <span className='navbar-brand'>Luv 2 Read</span>
                <button className='navbar-toggler'
                    type='button'
                    data-bs-toggle='collapse'
                    data-bs-target='#navbarNavDropdown'
                    aria-controls='navbarNavDropdown'
                    aria-expanded='false'>
                    <span className='navbar-toggler-icon'></span>
                </button>
                <div className='collapse navbar-collapse' id='navbarNavDropdown'>
                    <ul className='navbar-nav'>
                        <li className='nav-item'>
                            <NavLink className='nav-link' to='/home'>Home</NavLink>
                        </li>
                        <li className='nav-item'>
                            <NavLink className='nav-link' to='/search'>Search Books</NavLink>
                        </li>
                        {isAuthenticated && user && (
                            <li className='nav-item'>
                                <NavLink className='nav-link' to='/shelf'>Shelf</NavLink>
                            </li>
                        )}
                        {isAuthenticated && isAdmin && (
                            <li className='nav-item'>
                                <NavLink className='nav-link' to='/admin'>Admin</NavLink>
                            </li>
                        )}
                    </ul>
                    <ul className='navbar-nav ms-auto'>
                        {!isAuthenticated && (
                            <li className='nav-item m-1'>
                                <LoginButton />

                            </li>
                        )}
                        {isAuthenticated && (
                            <li className='nav-item m-1'>
                                <LogoutButton />
                            </li>
                        )}
                    </ul>
                </div>
            </div>
        </nav>
    );
};
