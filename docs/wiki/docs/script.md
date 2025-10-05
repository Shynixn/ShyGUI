# Script Language

A simple expression-based scripting language for evaluating conditions and performing basic computations.

## Data Types

The language supports three fundamental data types:

* **String**: Text values enclosed in double quotes
* **Number**: Numeric values (integers and decimals)
* **Boolean**: `true` or `false` values

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
* `!` (NOT)
* `>`, `>=`, `<`, `<=` (Comparisons)
* `==`, `!=` (Equality)
* `&&` (AND)
* `||` (OR)

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

### Real-World Examples from GUIs

**Pet State Checking:**
```javascript
// Check if pet is spawned (from petblocks_main_menu.yml)
"%petblocks_pet_isSpawned_selected%" == "true"

// Check if pet is not spawned
"%petblocks_pet_isSpawned_selected%" == "false"

// Check if pet is mounted
"%petblocks_pet_isMounted_selected%" == "true"
```

**Player Permission/Group Checks:**
```javascript
// Check if player is admin
CONTAINS_IGNORE_CASE("%vault_group%", "admin")

// Check if player is VIP or higher
CONTAINS_IGNORE_CASE("%vault_group%", "vip") || CONTAINS_IGNORE_CASE("%vault_group%", "admin")

// Check player name contains specific text
CONTAINS("%shygui_player_name%", "Mario")
```

**Economic Conditions:**
```javascript
// Check if player can afford an item
NUMBER("%vault_eco_balance%") >= 1000

// Check if player has enough points
NUMBER("%playerpoints_points%") >= 500

// Verify minimum balance for VIP features
NUMBER("%vault_eco_balance%") >= 10000 && CONTAINS_IGNORE_CASE("%vault_group%", "vip")
```

**Placeholder Validation:**
```javascript
// Check if placeholder exists and is not empty (from petblocks examples)
!CONTAINS("%petblocks_pet_itemType_selected%", "petblocks_pet_itemType_selected")

// Verify GUI parameter is provided
LENGTH("%shygui_gui_param1%") > 0

// Check if player has balance data
!CONTAINS("%vault_eco_balance%", "vault_eco_balance")
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

* **Use parentheses** to make complex expressions more readable
* **Validate input** before passing to numeric functions
* **Use appropriate case** for function names (they're case-insensitive but UPPERCASE is conventional)
* **Handle exceptions** when evaluating user-provided expressions
* **Test edge cases** like empty strings and zero values

## Limitations

* No arithmetic operators (`+`, `-`, `*`, `/`) - use functions for calculations
* No variable assignment - expressions are stateless
* No custom function definitions
* Limited to expression evaluation (no statements or control flow)
* String escape sequences are limited (only `\"` is supported)