package a1;

import org.jetbrains.annotations.NotNull;

public class Color {

    private String name;

    public Color(String s) {
        this.name = s;
    }

    public boolean equals(@NotNull String s) { //lets us check which color it is by name
        return this.getName().equals(s);
    }

    public boolean equals(@NotNull Color c) { //colors are equivalent if they have same name
        return this.getName().equals(c.getName());
    }

    public String getName() {
        return name;
    }

}
