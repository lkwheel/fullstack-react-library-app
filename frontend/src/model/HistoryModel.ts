class HistoryModel {
    constructor(public id: string,
        public title: string,
        public author: string,
        public description: string,
        public category: string,
        public img: string,
        public userEmail: string,
        public checkoutDate: string,
        public returnedDate: string) { }
}

export default HistoryModel;