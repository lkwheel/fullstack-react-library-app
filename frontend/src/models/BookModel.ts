class BookModel {

    constructor(public id: string,
        public title: string,
        public author?: string,
        public description?: string,
        public copies?: number,
        public copiesAvailable?: number,
        public category?: string,
        public img?: string) {
    }
}

export default BookModel;