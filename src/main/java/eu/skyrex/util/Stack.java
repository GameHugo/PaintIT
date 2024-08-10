package eu.skyrex.util;

public class Stack<T> {

    private Stack<T> parent;
    private Stack<T> child;
    private T value;

    public Stack(T value) {
        this.value = value;
    }

    /**
     * Appends a new value to the stack
     *
     * If this instance already has a child, this child will be discarded.
     *
     * @param value the value to append
     * @return the new stack
     */
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
