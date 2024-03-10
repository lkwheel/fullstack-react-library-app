class HistoryModel {
    id: number;
    title: string;
    author: string;
    description: string;
    category: string;
    img: string;
    userEmail: string;
    checkoutDate: string;
    returnedDate: string;

    constructor(id: number, title: string, author: string, description: string, category: string,
        img: string, userEmail: string, checkoutDate: string, returnedDate: string) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.category = category;
        this.img = img;
        this.userEmail = userEmail;
        this.checkoutDate = checkoutDate;
        this.returnedDate = returnedDate;
    }
}

export default HistoryModel;