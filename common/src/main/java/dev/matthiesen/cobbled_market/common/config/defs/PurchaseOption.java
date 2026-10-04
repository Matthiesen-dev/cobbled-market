package dev.matthiesen.cobbled_market.common.config.defs;

import com.electronwill.nightconfig.core.Config;

public record PurchaseOption(int quantity, int price) {
    public PurchaseOption {
        if (quantity <= 0 || price < 0) {
            throw new IllegalArgumentException("Purchase quantity must be positive and price must be nonnegative");
        }
    }

    public static boolean isValid(Config config) {
        return isIntegerInRange(config.get("quantity"), 1)
                && isIntegerInRange(config.get("price"), 0);
    }

    private static boolean isIntegerInRange(Object value, int minimum) {
        if (!(value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long)) {
            return false;
        }
        long number = ((Number) value).longValue();
        return number >= minimum && number <= Integer.MAX_VALUE;
    }

    public static PurchaseOption deserialize(Config config) {
        if (!isValid(config)) {
            throw new IllegalArgumentException("Invalid purchase option: quantity and price must be integers in range");
        }
        return new PurchaseOption(config.<Number>get("quantity").intValue(), config.<Number>get("price").intValue());
    }

    public Config serialize() {
        Config config = Config.inMemory();
        config.set("quantity", quantity);
        config.set("price", price);
        return config;
    }
}
