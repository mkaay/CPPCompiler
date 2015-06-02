package group11.typechecker;

enum TypeCode {
    BOOL {
        public String toString() {
            return "bool";
        }
    },
    INT {
        public String toString() {
            return "int";
        }
    },
    DOUBLE {
        public String toString() {
            return "double";
        }
    },
    VOID {
        public String toString() {
            return "void";
        }
    },
    STRING {
        public String toString() {
            return "string";
        }
    }
}
