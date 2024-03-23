import { useAuth0 } from '@auth0/auth0-react';
import { useEffect, useState } from 'react';
import MessageModel from '../../../models/MessageModel';
import { SpinnerLoading } from '../../Utils/SpinnerLoading';
import { Pagination } from '../../Utils/Pagination';

export const Messages = () => {

    const apiUrl = process.env.REACT_APP_API_URL;
    const { user, isAuthenticated, getAccessTokenSilently } = useAuth0();

    const [isLoadingMessages, setIsLoadingMessages] = useState(true);

    const [httpError, setHttpError] = useState(null);

    // Messages
    const [messages, setMessages] = useState<MessageModel[]>([]);

    // Pagination
    const [messagesPerPage] = useState(5);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        const fetchUserMessages = async () => {
            if (isAuthenticated && user?.email) {
                const apiAccessToken = await getAccessTokenSilently();
                const baseUrl = `${apiUrl}/messages/protected/find-by-user-email`;
                const url = `${baseUrl}?page=${currentPage - 1}&size=${messagesPerPage}`;

                const requestOptions = {
                    method: 'GET',
                    headers: {
                        'content-type': 'application/json',
                        'Authorization': `Bearer ${apiAccessToken}`,
                    }
                };
                const response = await fetch(url, requestOptions);
                if (!response.ok) {
                    throw new Error('Something went wrong - fetching user messages');
                }

                const responseJson = await response.json();
                setMessages(responseJson.content);
                setTotalPages(responseJson.totalPages);
                setIsLoadingMessages(false);
            }
        };
        fetchUserMessages().catch((error: any) => {
            setIsLoadingMessages(false);
            setHttpError(error.messages);
        });
        window.scrollTo(0, 0);
    }, [apiUrl, getAccessTokenSilently, currentPage, isAuthenticated, user, messagesPerPage]);

    if (isLoadingMessages) {
        return (
            <SpinnerLoading />
        );
    }

    if (httpError) {
        return (
            <div className='container m-5'>
                <p>{httpError}</p>
            </div>
        )
    }

    const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

    return (
        <div className='mt-2'>
            {messages.length > 0 ?
                <>
                    <h5>Current Q/A: </h5>
                    {messages.map(message => (
                        <div key={message.id}>
                            <div className='card mt-3 shadow p-3 bg-body rounded'>
                                <h5>Case id: <span className='badge bg-secondary'>{message.id}</span>: {message.title}</h5>
                                <h6>{message.question}</h6>
                                <hr />
                                <div>
                                    <h5>Response: </h5>
                                    {message.response && message.adminEmail ?
                                        <>
                                            <h6>{message.adminEmail} (admin)</h6>
                                            <p>{message.response}</p>
                                        </>
                                        :
                                        <>
                                            <p><i>Pending response from admin. Please be patient.</i></p>
                                        </>
                                    }
                                </div>
                            </div>

                        </div>
                    ))}
                </>
                :
                <>
                    <h5>All questions you submit will be shown here</h5>
                </>
            }
            {totalPages > 1 && <Pagination
                currentPage={currentPage} totalPages={totalPages}
                paginate={paginate} />
            }
        </div>
    );
}