import { useAuth0 } from '@auth0/auth0-react';
import { useEffect, useState } from 'react';
import MessageModel from '../../../model/MessageModel';
import { SpinnerLoading } from '../../Utils/SpinnerLoading';
import { Pagination } from '../../Utils/Pagination';
import { AdminMessage } from './AdminMessage';

export const AdminMessages = () => {
    const { user, isAuthenticated, getAccessTokenSilently } = useAuth0();

    const [isAdmin, setIsAdmin] = useState(false);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [httpError, setHttpError] = useState(null);

    // Messages endpoint state
    const [messages, setMessages] = useState<MessageModel[]>([]);
    const [messagesPerPage] = useState(5);

    // Pagination
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        const fetchMessages = async () => {
            if (isAuthenticated && user?.email) {
                const apiAccessToken = await getAccessTokenSilently();
                const baseUrl = `http://localhost:6060/api/messages/protected/find-by-closed?closed=false`;
                const url = `${baseUrl}&page=${currentPage - 1}&size=5`;

                const requestOptions = {
                    method: 'GET',
                    headers: {
                        'content-type': 'application/json',
                        'Authorization': `Bearer ${apiAccessToken}`,
                    }
                };
                const response = await fetch(url, requestOptions);
                if (!response.ok) {
                    throw new Error('Something went wrong fetching messages');
                }

                const responseJson = await response.json();
                setMessages(responseJson.content);
                setTotalPages(responseJson.totalPages);
                setIsLoading(false);
            }

        };
        fetchMessages().catch((error: any) => {
            setIsLoading(false);
            setHttpError(error.message);
        });
    }, [getAccessTokenSilently, currentPage]);

    if (isLoading) {
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
        <div className='mt-3'>
            {messages.length > 0 ?
                <>
                    <h5>Pending Q/A:</h5>
                    {messages.map(message => (
                        <AdminMessage message={message} key={message.id} />
                    ))}
                </>
                :
                <h5>No pending Q/A</h5>
            }
            {totalPages > 1 && <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                paginate={paginate} />}
        </div>
    );
}