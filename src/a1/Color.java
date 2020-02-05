package a1;

import org.jetbrains.annotations.NotNull;

public class Color {

    private String name;

    public Color(String s) {
        this.name = s;
    }

    public boolean equals(Color c) { //colors are equivalent if they have same name
        if (c == null) {return false;}
        return this.getName().equals(c.getName());
    }

    public String getName() {
        return name;
    }

}
