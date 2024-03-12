import { Redirect, Route, Switch } from 'react-router-dom';
import './App.css';
import { ProtectedRoute } from './ProtectedRoute/ProtectedRoute';
import { BookCheckoutPage } from './layouts/BookCheckoutPage/BookCheckoutPage';
import { ReviewListPage } from './layouts/BookCheckoutPage/ReviewListPage/ReviewListPage';
import { CallbackPage } from './layouts/CallbackPage/CallbackPage';
import { HomePage } from './layouts/HomePage/HomePage';
import { Footer } from './layouts/NavbarAndFooter/Footer';
import { Navbar } from './layouts/NavbarAndFooter/Navbar';
import { SearchBooksPage } from './layouts/SearchBooksPage/SearchBooksPage';
import { ShelfPage } from './layouts/ShelfPage/ShelfPage';
import { MessagesPage } from './layouts/MessagesPage/MessagesPage';

export const App = () => {
  return (
    <div className='d-flex flex-column min-vh-100'>
      <Navbar />
      <div className='flex-grow-1'>
        <Switch>
          <Route path='/' exact>
            <Redirect to='/home' />
          </Route>

          <Route path='/home'>
            <HomePage />
          </Route>

          <Route path='/callback'>
            <CallbackPage />
          </Route>

          <Route path='/search'>
            <SearchBooksPage />
          </Route>

          <Route path='/reviewList/:bookId'>
            <ReviewListPage />
          </Route>

          <Route path='/checkout/:bookId'>
            <BookCheckoutPage />
          </Route>

          <ProtectedRoute path='/shelf' component={ShelfPage} />
          <ProtectedRoute path='/messages' component={MessagesPage} />

        </Switch>
      </div>
      <Footer />
    </div>
  );
}
