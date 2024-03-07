import { useAuth0 } from '@auth0/auth0-react';
import React from 'react';
import { HomePage } from '../HomePage/HomePage';

export const CallbackPage: React.FC = () => {
    const { error } = useAuth0();

    if (error) {
        return (
            <div>
                <div className='d-none d-lg-block'>
                    <div className='row g-0 mt-5'>
                        <h1 id='page-title' className='content__title'>
                            Error
                        </h1>
                        <div className='row g-0 mt-5'>
                            <p id='page-description' className='lead'>
                                <span>{error.message}</span>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <HomePage />
    );
};