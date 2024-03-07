import { useAuth0 } from '@auth0/auth0-react';
import React from 'react';

export const LogoutButton: React.FC = () => {
    const { logout } = useAuth0();

    const handleLogout = () => {
        logout({
            logoutParams: {
                returnTo: window.location.origin,
            },
        });
    };

    return (
        <button className='btn btn-outline-light' onClick={handleLogout}>
            Sign Out
        </button>
    );
};