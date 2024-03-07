import React from 'react';
import { useAuth0 } from '@auth0/auth0-react';

export const LoginButton = () => {
    const { loginWithRedirect } = useAuth0();

    const handleLogin = async () => {
        await loginWithRedirect({
            appState: {
                returnTo: '/',
            },
            authorizationParams: {
                prompt: 'login',
            },
        });
    };

    return (
        <button
            className='btn btn-outline-light'
            onClick={() => handleLogin()}
        >
            Sign In
        </button>
    );
};