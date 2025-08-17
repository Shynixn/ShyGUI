# Script Language

A simple expression-based scripting language for evaluating conditions and performing basic computations.

## Data Types

The language supports three fundamental data types:

- **String**: Text values enclosed in double quotes
- **Number**: Numeric values (integers and decimals)
- **Boolean**: `true` or `false` values

## Literals

### String Literals
```
"Hello World"
"This is a string"
""  // Empty string
```

### Number Literals
```
42
3.14159
0
-5.5
```

### Boolean Literals
```
true
false
TRUE    // Case insensitive
False   // Case insensitive
```

## Operators

### Comparison Operators
```
==    // Equal to
!=    // Not equal to
>     // Greater than
>=    // Greater than or equal to
<     // Less than
<=    // Less than or equal to
```

### Logical Operators
```
&&    // AND
||    // OR
!     // NOT
```

### Operator Precedence (highest to lowest)
1. `!` (NOT)
2. `>`, `>=`, `<`, `<=` (Comparisons)
3. `==`, `!=` (Equality)
4. `&&` (AND)
5. `||` (OR)

## Built-in Functions

### String Functions

#### `STRING(value)`
Converts any value to a string.
```
STRING(42)        // "42"
STRING(true)      // "true"
STRING(3.14)      // "3.14"
```

#### `UPPER(text)`
Converts text to uppercase.
```
UPPER("hello")    // "HELLO"
```

#### `LOWER(text)`
Converts text to lowercase.
```
LOWER("WORLD")    // "world"
```

#### `LENGTH(text)`
Returns the length of a string.
```
LENGTH("hello")   // 5
LENGTH("")        // 0
```

#### `CONTAINS(haystack, needle)`
Checks if a string contains another string (case-sensitive).
```
CONTAINS("hello world", "world")  // true
CONTAINS("Hello", "hello")        // false
```

#### `CONTAINS_IGNORE_CASE(haystack, needle)`
Checks if a string contains another string (case-insensitive).
```
CONTAINS_IGNORE_CASE("Hello World", "hello")  // true
```

### Numeric Functions

#### `NUMBER(value)`
Converts a value to a number.
```
NUMBER("42")      // 42.0
NUMBER("3.14")    // 3.14
NUMBER(true)      // 1.0
NUMBER(false)     // 0.0
```

#### `ABS(number)`
Returns the absolute value of a number.
```
ABS(-5)           // 5.0
ABS(3.14)         // 3.14
```

#### `ROUND(number, decimal_places)`
Rounds a number to the specified number of decimal places.
```
ROUND(3.14159, 2)  // 3.14
ROUND(42.7, 0)     // 43.0
```

## Examples


### Basic Comparisons
```javascript
5 > 3                    // true
"apple" == "orange"      // false
LENGTH("test") >= 4      // true
```

### String Operations
```javascript
CONTAINS("minecraft", "craft")                    // true
UPPER("hello") == "HELLO"                        // true
CONTAINS_IGNORE_CASE("Player123", "player")     // true
```

### Using PlaceHolders in Expressions

```javascript
CONTAINS("%shygui_player_name%", "Mario")        // If the current player has got 'Mario' in his name.
UPPER("%shygui_player_name%") == "MARIO"        // true
CONTAINS_IGNORE_CASE("%shygui_player_name%", "MaRiO")     // true
```

### Logical Operations
```javascript
true && false                           // false
5 > 3 || 2 < 1                        // true
!CONTAINS("hello", "world")            // true
LENGTH("test") > 0 && true             // true
```

### Complex Expressions
```javascript
// Check if a string is not empty and contains specific text
LENGTH("player_name") > 0 && CONTAINS_IGNORE_CASE("player_name", "admin")

// Validate numeric range
NUMBER("25") >= 18 && NUMBER("25") <= 65

// String validation with multiple conditions
UPPER("input") == "YES" || UPPER("input") == "Y" || UPPER("input") == "TRUE"

// Function composition
ROUND(ABS(-3.14159), 2) == 3.14
```

### Parentheses for Precedence
```javascript
(5 + 3) > 6              // true (if arithmetic was supported)
!(false || true)         // false
CONTAINS("test", "t") && (true || false)  // true
```

## Best Practices

1. **Use parentheses** to make complex expressions more readable
2. **Validate input** before passing to numeric functions
3. **Use appropriate case** for function names (they're case-insensitive but UPPERCASE is conventional)
4. **Handle exceptions** when evaluating user-provided expressions
5. **Test edge cases** like empty strings and zero values

## Limitations

- No arithmetic operators (`+`, `-`, `*`, `/`) - use functions for calculations
- No variable assignment - expressions are stateless
- No custom function definitions
- Limited to expression evaluation (no statements or control flow)
- String escape sequences are limited (only `\"` is supported)