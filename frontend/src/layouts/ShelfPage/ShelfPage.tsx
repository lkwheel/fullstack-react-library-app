import { Loans } from "./components/Loans";

export const ShelfPage = () => {
    return (
        <div className='container'>
            <div className='mt-3'>
                <nav>
                    <div className='nav nav-tabs' id='nav-tab' role='tablist'>
                        <button id='nav-loans-tab'
                            className='nav-link active'
                            data-bs-toggle='tab'
                            data-bs-target='#nav-loans'
                            type='button'
                            role='tab'
                            aria-controls='nav-loans'
                            aria-selected='true'>
                            Loans
                        </button>
                        <button id='nav-history-tab'
                            className='nav-link'
                            data-bs-toggle='tab'
                            data-bs-target='#nav-history'
                            type='button'
                            role='tab'
                            aria-controls='nav-history'
                            aria-selected='false'>
                            Your History
                        </button>
                    </div>
                </nav>
                <div className='tab-content' id='nav-tabContent'>
                    <div className='tab-pane fade show active'
                        id='nav-loans'
                        role='tabpanel'
                        aria-labelledby='nav-loans-tab'>
                        <Loans />
                    </div>
                    <div className='tab-pane fade'
                        id='nav-history'
                        role='tabpanel'
                        aria-labelledby='nav-history-tab'>
                        <p>Checkout History</p>
                    </div>
                </div>
            </div>
        </div>
    );
}