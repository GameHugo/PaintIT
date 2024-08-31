package eu.skyrex.maps;

public record Pixel(int x, int y) {
    public Pixel add(Pixel other) {
        return new Pixel(x + other.x, y + other.y);
    }

    public Pixel sub(Pixel other) {
        return new Pixel(x - other.x, y - other.y);
    }

    public Pixel mul(double factor) {
        return new Pixel((int) (x * factor), (int) (y * factor));
    }

    public double distance(Pixel other) {
        return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }

    public Pixel normalize() {
        final double length = Math.sqrt(x * x + y * y);
        return new Pixel((int) (x / length), (int) (y / length));
    }
}
