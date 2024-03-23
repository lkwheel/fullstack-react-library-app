import { useAuth0 } from '@auth0/auth0-react';
import { useEffect, useState } from 'react';
import MessageModel from '../../../model/MessageModel';
import { SpinnerLoading } from '../../Utils/SpinnerLoading';
import { Pagination } from '../../Utils/Pagination';
import { AdminMessage } from './AdminMessage';
import AdminMessageRequest from '../../../model/AdminMessageRequest';

export const AdminMessages = () => {

    const apiUrl = process.env.REACT_APP_API_URL;
    const { user, isAuthenticated, getAccessTokenSilently } = useAuth0();

    const [isAdmin, setIsAdmin] = useState(false);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [httpError, setHttpError] = useState(null);

    // Messages endpoint state
    const [messages, setMessages] = useState<MessageModel[]>([]);

    // Pagination
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(0);

    // Recall useEffect
    const [btnSubmit, setBtnSubmit] = useState(false);

    useEffect(() => {
        const fetchCheckAdminUseRole = async () => {
            if (isAuthenticated && user?.email) {
                try {
                    const apiAccessToken = await getAccessTokenSilently();
                    const url = `${apiUrl}/user/protected/permissions`;
                    const requestOptions = {
                        method: 'GET',
                        headers: {
                            'content-type': 'application/json',
                            'Authorization': `Bearer ${apiAccessToken}`,
                        }
                    };
                    const response = await fetch(url, requestOptions);
                    if (!response.ok) {
                        throw new Error('Something went wrong getting user permissions');
                    }
                    setIsAdmin(true);
                    setIsLoading(false);
                } catch (error: any) {
                    setIsAdmin(false);
                    setHttpError(error.message);
                    setIsLoading(false);
                }
            } else {
                setIsAdmin(false);
                setIsLoading(false);
            }
        };

        fetchCheckAdminUseRole();
    }, [apiUrl, getAccessTokenSilently, isAuthenticated, user, isAdmin]);

    useEffect(() => {
        const fetchMessages = async () => {
            if (isAuthenticated && user?.email) {
                const apiAccessToken = await getAccessTokenSilently();
                const baseUrl = `${apiUrl}/messages/protected/find-by-closed?closed=false`;
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
    }, [apiUrl, getAccessTokenSilently, isAuthenticated, user, currentPage, btnSubmit]);

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

    async function submitResponseToQuestion(id: string, adminResponse: string) {
        if (isAuthenticated && user?.email && isAdmin && id !== null && adminResponse !== '') {
            const url = `${apiUrl}/messages/protected/admin/message`;
            const apiAccessToken = await getAccessTokenSilently();
            const messageAdminRequestModel: AdminMessageRequest = new AdminMessageRequest(id, adminResponse);
            const requestOptions = {
                method: 'PUT',
                headers: {
                    'content-type': 'application/json',
                    'Authorization': `Bearer ${apiAccessToken}`,
                },
                body: JSON.stringify(messageAdminRequestModel)
            };
            const messageAdminRequestResponse = await fetch(url, requestOptions);
            if (!messageAdminRequestResponse.ok) {
                throw new Error('Something went wrong submitting response');
            }
            setBtnSubmit(!btnSubmit);
        }
    }

    return (
        <div className='mt-3'>
            {messages.length > 0 ?
                <>
                    <h5>Pending Q/A:</h5>
                    {messages.map(message => (
                        <AdminMessage message={message} key={message.id} submitResponseToQuestion={submitResponseToQuestion} />
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