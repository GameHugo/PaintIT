package eu.skyrex.util;

public class Stack<T> {

    private Stack<T> parent;
    private Stack<T> child;
    private T value;

    public Stack(T value) {
        this.value = value;
    }

    public Stack<T> append(T value) {
        Stack<T> stack = new Stack<>(value);
        stack.parent = this;
        child = stack;
        return stack;
    }

    public T getValue() {
        return value;
    }

    public Stack<T> getParent() {
        return parent;
    }

    public T findFirst() {
        Stack<T> stack = this;
        while (stack.parent != null) {
            stack = stack.parent;
        }
        return stack.value;
    }

    public Stack<T> getChild() {
        return child;
    }
}
