# HotKeyBind

**Forge 1.16.5** mod that lets you bind any keyboard key to any chat command or message.

## Features
- Bind any key (with optional modifier) to any command like `/ah`, `/home`, `/say hello`
- Multiple macro types: **SingleUse**, **Repeating**, **Delayed**, **ToggledRepeating**, **DisplayOnly**
- Clean custom GUI — open with **`** (grave accent) by default
- Config saved to `config/hotkeybind/macros.json`
- Works on any server, client-side only

## Installation
Drop `hotkeybind-1.5.3-forge-1.16.5.jar` into your `mods` folder.
Requires **Forge 1.16.5**.

## Usage
1. Press **`** (grave accent) to open the macro editor
2. Click **Добавить макрос** to add a new macro
3. Set the command (e.g. `/ah` or `hello world`)
4. Click **Клавиша** and press the key you want to bind
5. Choose macro type and click **Сохранить**

## Macro Types
| Type | Behaviour |
|------|-----------|
| SingleUse | Fires once per key press |
| Repeating | Fires repeatedly while key is held (use Delay in ms) |
| Delayed | Fires once after holding for N ms |
| ToggledRepeating | Toggle on/off with key, repeats while active |
| DisplayOnly | No action, label only |

## Building from source
```
./gradlew build
```
Output: `build/libs/hotkeybind-1.5.3-forge-1.16.5.jar`
