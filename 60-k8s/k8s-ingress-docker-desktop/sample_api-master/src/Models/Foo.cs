namespace routing.Models {
    public class Foo {

        public Foo(string reference)
        {
            Reference = reference;
        }

        public string Type => "Foo";
        public string Reference {get;set;}
    }
}