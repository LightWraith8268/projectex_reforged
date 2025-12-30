#!/usr/bin/env python3
"""
Generate Matter-tiered textures for ProjectEX Reforged
- Energy Link textures (color-tinted from base)
- Matter Block textures (solid glowing blocks)
"""

from PIL import Image
import os

# Matter tier colors (Minecraft dye colors + custom for special tiers)
MATTER_COLORS = {
    'basic': (85, 255, 255),      # Diamond cyan
    'dark': (25, 25, 25),          # Dark matter (very dark gray)
    'red': (153, 51, 51),          # Red matter
    'magenta': (178, 76, 216),     # Magenta dye
    'pink': (242, 127, 165),       # Pink dye
    'purple': (137, 50, 184),      # Purple dye
    'violet': (118, 68, 188),      # Between purple and blue
    'blue': (53, 57, 157),         # Blue dye
    'cyan': (76, 153, 178),        # Cyan dye
    'green': (94, 124, 22),        # Green dye
    'lime': (128, 199, 31),        # Lime dye
    'yellow': (254, 216, 61),      # Yellow dye
    'orange': (216, 127, 51),      # Orange dye
    'white': (249, 255, 254),      # White dye
    'fading': (220, 220, 255),     # Fading (light purple-white)
    'final': (255, 215, 0),        # The Final (gold)
}

# Paths
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
TEXTURES_DIR = os.path.join(BASE_DIR, 'src', 'main', 'resources', 'assets', 'projectex_reforged', 'textures', 'block')
ENERGY_LINK_BASE = os.path.join(TEXTURES_DIR, 'energy_link.png')
ENERGY_LINK_DIR = os.path.join(TEXTURES_DIR, 'energy_link')
MATTER_BLOCK_DIR = os.path.join(TEXTURES_DIR, 'matter_block')

def ensure_dir(path):
    """Create directory if it doesn't exist"""
    os.makedirs(path, exist_ok=True)

def tint_texture(base_img, color):
    """Apply color tint to texture"""
    img = base_img.copy().convert('RGBA')
    pixels = img.load()

    for y in range(img.height):
        for x in range(img.width):
            r, g, b, a = pixels[x, y]
            if a > 0:  # Only tint non-transparent pixels
                # Multiply blend mode with color
                pixels[x, y] = (
                    int(r * color[0] / 255),
                    int(g * color[1] / 255),
                    int(b * color[2] / 255),
                    a
                )

    return img

def create_glowing_block(color, size=16):
    """Create a solid glowing block texture"""
    img = Image.new('RGBA', (size, size))
    pixels = img.load()

    # Create gradient effect (brighter in center, darker at edges)
    for y in range(size):
        for x in range(size):
            # Distance from center (0.0 to 1.0)
            dx = abs(x - size/2) / (size/2)
            dy = abs(y - size/2) / (size/2)
            dist = max(dx, dy)

            # Brightness factor (1.0 in center, 0.7 at edges)
            brightness = 1.0 - (dist * 0.3)

            # Apply brightness to color
            r = int(min(255, color[0] * brightness * 1.2))  # Boost for glow
            g = int(min(255, color[1] * brightness * 1.2))
            b = int(min(255, color[2] * brightness * 1.2))

            pixels[x, y] = (r, g, b, 255)

    return img

def generate_energy_link_textures():
    """Generate color-tinted Energy Link textures for all Matter tiers"""
    print("Generating Energy Link textures...")

    # Load base texture
    if not os.path.exists(ENERGY_LINK_BASE):
        print(f"ERROR: Base texture not found: {ENERGY_LINK_BASE}")
        return

    base_img = Image.open(ENERGY_LINK_BASE)
    ensure_dir(ENERGY_LINK_DIR)

    for matter, color in MATTER_COLORS.items():
        print(f"  - {matter}.png (color: {color})")
        tinted = tint_texture(base_img, color)
        output_path = os.path.join(ENERGY_LINK_DIR, f'{matter}.png')
        tinted.save(output_path)

    print(f"[OK] Generated {len(MATTER_COLORS)} Energy Link textures")

def generate_matter_block_textures():
    """Generate solid glowing Matter Block textures for all tiers"""
    print("\nGenerating Matter Block textures...")
    ensure_dir(MATTER_BLOCK_DIR)

    for matter, color in MATTER_COLORS.items():
        print(f"  - {matter}.png (color: {color})")
        glowing = create_glowing_block(color)
        output_path = os.path.join(MATTER_BLOCK_DIR, f'{matter}.png')
        glowing.save(output_path)

    print(f"[OK] Generated {len(MATTER_COLORS)} Matter Block textures")

def main():
    print("=" * 60)
    print("ProjectEX Reforged - Texture Generator")
    print("=" * 60)

    generate_energy_link_textures()
    generate_matter_block_textures()

    print("\n" + "=" * 60)
    print("[OK] All textures generated successfully!")
    print("=" * 60)

if __name__ == '__main__':
    main()
