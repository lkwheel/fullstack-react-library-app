import { useAuth0 } from '@auth0/auth0-react';
import { useState } from 'react';
import AddBookRequest from '../../../models/AddBookRequest';

export const AddNewBook = () => {

    const apiUrl = process.env.REACT_APP_API_URL;
    const { user, isAuthenticated, getAccessTokenSilently } = useAuth0();

    // New Book
    const [title, setTitle] = useState('');
    const [author, setAuthor] = useState('');
    const [description, setDescription] = useState('');
    const [copies, setCopies] = useState(0);
    const [category, setCategory] = useState('Book Category');
    const [selectedImage, setSelectedImage] = useState<string | ArrayBuffer | null>(null);

    // Displays
    const [displayWarning, setDisplayWarning] = useState(false);
    const [displaySuccess, setDisplaySuccess] = useState(false);

    function categoryField(value: string) {
        setCategory(value);
    }

    async function base64ConversionForImages(e: any) {
        if (e.target.files[0]) {
            getBase64(e.target.files[0]);
        }
    }

    function getBase64(file: any) {
        let reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = () => {
            setSelectedImage(reader.result)
        };
        reader.onerror = (error) => {
            console.log('Error reading image', error);
        };
    }

    async function submitNewBook() {
        if (isAuthenticated && user?.email && title !== '' && author !== ''
            && category !== 'Book Category' && description !== '' && copies >= 0) {
            const book: AddBookRequest = new AddBookRequest(title, author, description, copies, category);
            if (selectedImage && selectedImage !== null) {
                book.img = String(selectedImage);
            }
            try {
                const apiAccessToken = await getAccessTokenSilently();
                const url = `${apiUrl}/admin/protected/add/book`;
                const requestOptions = {
                    method: 'POST',
                    headers: {
                        'content-type': 'application/json',
                        'Authorization': `Bearer ${apiAccessToken}`,
                    },
                    body: JSON.stringify(book)
                };
                const response = await fetch(url, requestOptions);
                if (!response.ok) {
                    throw new Error('Something went wrong adding book');
                }

                setTitle('');
                setAuthor('');
                setDescription('')
                setCategory('Book Category');
                setCopies(0);
                setSelectedImage(null);
                setDisplayWarning(false);
                setDisplaySuccess(true);
            } catch (error: any) {
                setDisplayWarning(true);
                setDisplaySuccess(false);
                throw new Error('Something went wrong adding book');
            }
        } else {
            setDisplayWarning(true);
            setDisplaySuccess(false);
        }
    }

    return (
        <div className='container mt-5 mb-5'>
            {displaySuccess &&
                <div className='alert alert-success' role='alert'>
                    Book added successfully
                </div>
            }
            {displayWarning &&
                <div className='alert alert-danger' role='alert'>
                    All fields must be filled out
                </div>
            }
            <div className='card'>
                <div className='card-header'>
                    Add a new book
                </div>
                <div className='card-body'>
                    <form method='POST'>
                        <div className='row'>
                            <div className='col-md-6 mb-3'>
                                <label className='form-label'>Title</label>
                                <input type='text' className='form-control' name='title' required
                                    onChange={e => setTitle(e.target.value)} value={title} />
                            </div>
                            <div className='col-md-3 mb-3'>
                                <label className='form-label'>Author</label>
                                <input type='text' className='form-control' name='author' required
                                    onChange={e => setAuthor(e.target.value)} value={author} />
                            </div>
                            <div className='col-md-3 mb-3'>
                                <label className='form-label'>Category</label>
                                <button type='button' className='form-control btn btn-secondary dropdown-toggle'
                                    id='dropdownMenuButton1' data-bs-toggle='dropdown'
                                    aria-expanded='false'>
                                    {category}
                                </button>
                                <ul id='addNewBookId' className='dropdown-menu' aria-labelledby='dropdownMenuButton1'>
                                    <li><a onClick={() => categoryField('FE')} className='dropdown-item'>Front End</a></li>
                                    <li><a onClick={() => categoryField('BE')} className='dropdown-item'>Back End</a></li>
                                    <li><a onClick={() => categoryField('Data')} className='dropdown-item'>Data</a></li>
                                    <li><a onClick={() => categoryField('DevOps')} className='dropdown-item'>DevOps</a></li>
                                </ul>
                            </div>
                        </div>
                        <div className='col-md-12 mb-3'>
                            <label htmlFor='exampleFormControlTextarea1' className='form-label'>Description</label>
                            <textarea className='form-control' id='exampleFormControlTextarea1' rows={3}
                                onChange={(e) => setDescription(e.target.value)}
                                value={description}></textarea>
                        </div>
                        <div className='col-md-3 mb-3'>
                            <label htmlFor='copies' className='form-label'>Copies</label>
                            <input type='number' className='form-control' name='copies' id='copies' required
                                onChange={(e) => setCopies(Number(e.target.value))} value={copies} />
                        </div>
                        <input type='file' onChange={(e) => base64ConversionForImages(e)} />
                        <div>
                            <button type='button' className='btn btn-primary mt-3'
                                onClick={submitNewBook}>
                                Add Book
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}