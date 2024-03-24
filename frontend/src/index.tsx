import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import { App } from './App';
import { BrowserRouter } from 'react-router-dom';
import Auth0ProviderWithHistory from './Auth/Auth0ProviderWithHistory';
import { loadStripe } from '@stripe/stripe-js';
import { Elements } from '@stripe/react-stripe-js';

const stripePromise = loadStripe(`${process.env.REACT_APP_STRIPE_PUB_KEY}`);

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);
root.render(

  <BrowserRouter>
    <Auth0ProviderWithHistory>
      <Elements stripe={stripePromise}>
        <App />
      </Elements>
    </Auth0ProviderWithHistory>
  </BrowserRouter>
);
