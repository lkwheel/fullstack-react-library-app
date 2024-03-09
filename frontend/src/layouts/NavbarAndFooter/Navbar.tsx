import { NavLink } from 'react-router-dom';
import { LoginButton } from '../../Auth/LoginButton';
import { useAuth0 } from '@auth0/auth0-react';
import { LogoutButton } from '../../Auth/LogoutButton';

export const Navbar = () => {
    const { isAuthenticated } = useAuth0();
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
                        {isAuthenticated && (
                            <li className='nav-item'>
                                <NavLink className='nav-link' to='/shelf'>Shelf</NavLink>
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
