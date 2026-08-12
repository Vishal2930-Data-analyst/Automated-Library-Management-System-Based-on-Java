package style;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * ThemeManager — singleton that holds all theme colors.
 * All UI classes read colors from here instead of hardcoding.
 * When toggleTheme() is called, all registered listeners are notified
 * and every open window repaints itself automatically.
 *
 * USAGE IN ANY UI CLASS:
 *   ThemeManager tm = ThemeManager.getInstance();
 *   Color bg = tm.BG_PAGE;
 *
 * REGISTER A WINDOW FOR AUTO-REPAINT:
 *   ThemeManager.getInstance().addListener(frame::repaint);
 *
 * TOGGLE FROM DASHBOARD:
 *   ThemeManager.getInstance().toggleTheme();
 */
public class ThemeManager {

    // ── Singleton ────────────────────────────────────────────────────
    private static final ThemeManager INSTANCE = new ThemeManager();
    public static ThemeManager getInstance() { return INSTANCE; }

    // ── Theme state ──────────────────────────────────────────────────
    private boolean darkMode = false;

    // ── Listeners ────────────────────────────────────────────────────
    public interface ThemeListener { void onThemeChanged(); }
    private final List<ThemeListener> listeners = new ArrayList<ThemeListener>();

    // ════════════════════════════════════════════════════════════════
    //  PUBLIC COLORS — always read from here, never hardcode in UI
    // ════════════════════════════════════════════════════════════════

    // Backgrounds
    public Color BG_PAGE;
    public Color BG_CARD;
    public Color BG_SIDEBAR;
    public Color BG_FIELD;

    // Brand
    public Color INDIGO;
    public Color INDIGO_LIGHT;
    public Color INDIGO_MED;
    public Color VIOLET;
    public Color EMERALD;
    public Color EMERALD_LIGHT;
    public Color ROSE;
    public Color ROSE_LIGHT;
    public Color AMBER;
    public Color AMBER_LIGHT;
    public Color SKY;
    public Color SKY_LIGHT;

    // Text
    public Color TEXT_H;
    public Color TEXT_BODY;
    public Color TEXT_MUTED;
    public Color TEXT_SUBTLE;

    // Borders / structure
    public Color BORDER;
    public Color ROW_STRIPE;
    public Color ROW_SELECT;
    public Color GLASS_WHITE;
    public Color GLASS_BORDER;

    // Toggle button colors
    public Color TOGGLE_TRACK;
    public Color TOGGLE_THUMB;
    public Color TOGGLE_LABEL;

    // ════════════════════════════════════════════════════════════════
    //  CONSTRUCTOR — initialize with light theme
    // ════════════════════════════════════════════════════════════════
    private ThemeManager() {
        applyLight();
    }

    // ════════════════════════════════════════════════════════════════
    //  TOGGLE
    // ════════════════════════════════════════════════════════════════
    public void toggleTheme() {
        darkMode = !darkMode;
        if (darkMode) applyDark();
        else          applyLight();
        notifyListeners();
    }

    public boolean isDark() { return darkMode; }

    // ════════════════════════════════════════════════════════════════
    //  LIGHT PALETTE
    // ════════════════════════════════════════════════════════════════
    private void applyLight() {
        BG_PAGE     = new Color(244, 245, 251);
        BG_CARD     = new Color(255, 255, 255);
        BG_SIDEBAR  = new Color(255, 255, 255);
        BG_FIELD    = new Color(249, 250, 255);

        INDIGO      = new Color( 79,  70, 229);
        INDIGO_LIGHT= new Color(238, 237, 255);
        INDIGO_MED  = new Color(165, 180, 252);
        VIOLET      = new Color(124,  58, 237);
        EMERALD     = new Color(  5, 150, 105);
        EMERALD_LIGHT=new Color(209, 250, 229);
        ROSE        = new Color(225,  29,  72);
        ROSE_LIGHT  = new Color(255, 228, 230);
        AMBER       = new Color(217, 119,   6);
        AMBER_LIGHT = new Color(254, 243, 199);
        SKY         = new Color(  2, 132, 199);
        SKY_LIGHT   = new Color(224, 242, 254);

        TEXT_H      = new Color( 17,  24,  39);
        TEXT_BODY   = new Color( 55,  65,  81);
        TEXT_MUTED  = new Color(107, 114, 128);
        TEXT_SUBTLE = new Color(156, 163, 175);

        BORDER      = new Color(226, 232, 240);
        ROW_STRIPE  = new Color(249, 249, 255);
        ROW_SELECT  = new Color(224, 221, 255);
        GLASS_WHITE = new Color(255, 255, 255, 210);
        GLASS_BORDER= new Color(255, 255, 255, 110);

        TOGGLE_TRACK= new Color(226, 232, 240);
        TOGGLE_THUMB= new Color(255, 255, 255);
        TOGGLE_LABEL= new Color(107, 114, 128);
    }

    // ════════════════════════════════════════════════════════════════
    //  DARK PALETTE
    // ════════════════════════════════════════════════════════════════
    private void applyDark() {
        BG_PAGE     = new Color( 10,  12,  22);
        BG_CARD     = new Color( 18,  22,  36);
        BG_SIDEBAR  = new Color( 13,  16,  28);
        BG_FIELD    = new Color( 24,  28,  46);

        // Keep brand colors vibrant in dark mode
        INDIGO      = new Color( 99,  90, 255);
        INDIGO_LIGHT= new Color( 30,  28,  60);
        INDIGO_MED  = new Color( 79,  70, 180);
        VIOLET      = new Color(150,  80, 255);
        EMERALD     = new Color( 16, 200, 140);
        EMERALD_LIGHT=new Color( 10,  50,  36);
        ROSE        = new Color(255,  70, 100);
        ROSE_LIGHT  = new Color( 60,  15,  25);
        AMBER       = new Color(255, 170,  30);
        AMBER_LIGHT = new Color( 55,  40,   8);
        SKY         = new Color( 40, 170, 240);
        SKY_LIGHT   = new Color( 10,  40,  65);

        TEXT_H      = new Color(230, 235, 255);
        TEXT_BODY   = new Color(180, 190, 215);
        TEXT_MUTED  = new Color(110, 120, 150);
        TEXT_SUBTLE = new Color( 70,  80, 110);

        BORDER      = new Color( 35,  42,  68);
        ROW_STRIPE  = new Color( 22,  28,  44);
        ROW_SELECT  = new Color( 35,  30,  80);
        GLASS_WHITE = new Color( 25,  30,  50, 220);
        GLASS_BORDER= new Color( 60,  70, 120, 110);

        TOGGLE_TRACK= new Color( 79,  70, 229);
        TOGGLE_THUMB= new Color(255, 255, 255);
        TOGGLE_LABEL= new Color(160, 170, 210);
    }

    // ════════════════════════════════════════════════════════════════
    //  LISTENER MANAGEMENT
    // ════════════════════════════════════════════════════════════════
    public void addListener(ThemeListener l) {
        if (!listeners.contains(l)) listeners.add(l);
    }

    public void removeListener(ThemeListener l) {
        listeners.remove(l);
    }

    private void notifyListeners() {
        for (ThemeListener l : listeners) {
            try { l.onThemeChanged(); }
            catch (Exception e) { e.printStackTrace(); }
        }
    }

	public static void setDarkTheme() {
		// TODO Auto-generated method stub
		
	}
}