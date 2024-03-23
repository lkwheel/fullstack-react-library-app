import { useAuth0 } from '@auth0/auth0-react';
import { useState } from 'react';
import MessageModel from '../../../model/MessageModel';

export const PostNewMessage = () => {

    const apiUrl = process.env.REACT_APP_API_URL;
    const { user, isAuthenticated, getAccessTokenSilently } = useAuth0();
    const [title, setTitle] = useState('');
    const [question, setQuestion] = useState('');
    const [displayWarning, setDisplayWarning] = useState(false);
    const [displaySuccess, setDisplaySuccess] = useState(false);

    async function submitNewQuestion() {
        if (isAuthenticated && user?.email && title !== '' && question !== '') {
            const apiAccessToken = await getAccessTokenSilently();
            const url = `${apiUrl}/messages/protected/add`;
            const messageRequestModel: MessageModel = new MessageModel(title, question);
            const requestOptions = {
                method: 'POST',
                headers: {
                    'content-type': 'application/json',
                    'Authorization': `Bearer ${apiAccessToken}`,
                },
                body: JSON.stringify(messageRequestModel)
            };

            const newQuestionResponse = await fetch(url, requestOptions);
            if (!newQuestionResponse.ok) {
                throw new Error('Something went wrong - submit new question');
            }

            setTitle('');
            setQuestion('');
            setDisplayWarning(false);
            setDisplaySuccess(true);
        } else {
            setDisplayWarning(true);
            setDisplaySuccess(false);
        }
    }

    return (
        <div className='card mt-3'>
            <div className='card-header'>
                Ask question to Luv 2 Read Admin
            </div>
            <div className='card-body'>
                <form action='POST'>
                    {
                        displayWarning &&
                        <div className='alert alert-danger' role='alert'>
                            All fields must be filled in
                        </div>
                    }
                    {
                        displaySuccess &&
                        <div className='alert alert-success' role='alert'>
                            Question added successfully
                        </div>
                    }
                    <div className='mb-3'>
                        <label className='form-label'>Title</label>
                        <input id='exampleFormControlInput1'
                            type='text' className='form-control'
                            placeholder='Title'
                            onChange={e => setTitle(e.target.value)}
                            value={title} />
                    </div>
                    <div className='mb-3'>
                        <label className='form-label'>Question</label>
                        <textarea id='exampleFormControlTextArea1'
                            rows={3}
                            className='form-control'
                            placeholder='Question'
                            onChange={e => setQuestion(e.target.value)}
                            value={question} />
                    </div>
                    <div>
                        <button type='button' className='btn btn-primary mt-3'
                            onClick={submitNewQuestion}>
                            Submit a question
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}