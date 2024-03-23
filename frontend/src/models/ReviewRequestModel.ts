class ReviewRequestModel {

    constructor(public rating: number,
        public bookId: string,
        public reviewDescription: string) { }
}

export default ReviewRequestModel;