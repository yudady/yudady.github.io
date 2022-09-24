namespace routing.Models {
    public class Bar {

        public Bar(string reference)
        {
            Reference = reference;
        }

        public string Type => "Bar";
        public string Reference {get;set;}
    }
}