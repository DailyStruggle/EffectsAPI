I was reusing effect code, so this is some centralized code that automates modular effect storage and recall, with a few useful internal effects.

This is intended to function either as a standalone plugin or as a shaded dependency.

This improves upon my previous implementations by:
 - modularizing effects, with new functions for addition and removal
 - adding methods to run specific effects or all effects in a list
 - making this code more available, not deep in plugin files

