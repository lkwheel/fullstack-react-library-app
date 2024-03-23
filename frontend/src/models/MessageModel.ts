class MessageModel {
    constructor(
        public title: string,
        public question: string,
        public id?: string,
        public userEmail?: string,
        public adminEmail?: string,
        public response?: string,
        public closed?: boolean
    ) { }
}

export default MessageModel;