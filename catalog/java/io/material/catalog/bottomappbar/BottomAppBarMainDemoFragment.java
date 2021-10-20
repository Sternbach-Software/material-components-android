/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.material.catalog.bottomappbar;

import io.material.catalog.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.text.Editable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomappbar.BottomAppBarTopEdgeTreatment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.shape.CutCornerTreatment;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import io.material.catalog.feature.DemoFragment;
import io.material.catalog.feature.OnBackPressedHandler;
import io.material.catalog.themeswitcher.ThemeSwitcherHelper;

/** A fragment that displays the main Bottom App Bar demos for the Catalog app. */
public class BottomAppBarMainDemoFragment extends DemoFragment implements OnBackPressedHandler {
  public final String PREFERENCES = "mPREFERENCES";
  SharedPreferences sp;
  SharedPreferences.Editor spe;
  protected BottomAppBar bar;
  protected CoordinatorLayout coordinatorLayout;
  protected FloatingActionButton fab;
  protected MaterialButton applyAlpha;
  protected TextInputEditText alphaValue;
  protected TextView savedConfigurations;
  protected MaterialCardView card1;
  protected MaterialCardView card2;
  protected MaterialCardView card3;
  protected MaterialCardView card4;
  protected MaterialCardView card5;
  protected MaterialCardView card6;
  protected MaterialCardView card7;
  protected MaterialCardView card8;

  @Nullable private ThemeSwitcherHelper themeSwitcherHelper;
  private BottomSheetBehavior<View> bottomDrawerBehavior;

  @Override
  public void onCreate(@Nullable Bundle bundle) {
    super.onCreate(bundle);
    setHasOptionsMenu(true);

    // The theme switcher helper is used in an adhoc way with the toolbar since the BottomAppBar is
    // set as the action bar.
    themeSwitcherHelper = new ThemeSwitcherHelper(getParentFragmentManager());
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
    menuInflater.inflate(R.menu.demo_primary, menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    showSnackbar(menuItem.getTitle());
    return true;
  }

  @LayoutRes
  public int getBottomAppBarContent() {
    return R.layout.cat_bottomappbar_fragment;
  }
  /**
   * <p>Finds the n-th index within a String, handling {@code null}.
   * This method uses {@link String#indexOf(String)} if possible.</p>
   * <p>Note that matches may overlap<p>
   *
   * <p>A {@code null} CharSequence will return {@code -1}.</p>
   *
   * @param str  the CharSequence to check, may be null
   * @param searchStr  the CharSequence to find, may be null
   * @param ordinal  the n-th {@code searchStr} to find, overlapping matches are allowed.
   * @param lastIndex true if lastOrdinalIndexOf() otherwise false if ordinalIndexOf()
   * @return the n-th index of the search CharSequence,
   *  {@code -1} if no match or {@code null} string input
   */
  private static int ordinalIndexOf(final String str, final String searchStr, final int ordinal, final boolean lastIndex) {
    if (str == null || searchStr == null || ordinal <= 0) {
      return -1;
    }
    if (searchStr.length() == 0) {
      return lastIndex ? str.length() : 0;
    }
    int found = 0;
    // set the initial index beyond the end of the string
    // this is to allow for the initial index decrement/increment
    int index = lastIndex ? str.length() : -1;
    do {
      if (lastIndex) {
        index = str.lastIndexOf(searchStr, index - 1); // step backwards thru string
      } else {
        index = str.indexOf(searchStr, index + 1); // step forwards through string
      }
      if (index < 0) {
        return index;
      }
      found++;
    } while (found < ordinal);
    return index;
  }
  @Override
  public View onCreateDemoView(
      LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
    View view = layoutInflater.inflate(getBottomAppBarContent(), viewGroup, false);
    sp = PreferenceManager.getDefaultSharedPreferences(view.getContext());
    spe = sp.edit();
    Toolbar toolbar = view.findViewById(R.id.toolbar);
    toolbar.setTitle(getDefaultDemoTitle());
    themeSwitcherHelper.onCreateOptionsMenu(toolbar.getMenu(), getActivity().getMenuInflater());
    toolbar.setOnMenuItemClickListener(themeSwitcherHelper::onOptionsItemSelected);
    toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());

    coordinatorLayout = view.findViewById(R.id.coordinator_layout);
    bar = view.findViewById(R.id.bar);
    ((AppCompatActivity) getActivity()).setSupportActionBar(bar);
    savedConfigurations = view.findViewById(R.id.saved_configurations);
    savedConfigurations.setText(sp.getString(PREFERENCES, "No configurations saved yet."));
    setUpBottomDrawer(view);

    fab = view.findViewById(R.id.fab);
    NavigationView navigationView = view.findViewById(R.id.navigation_view);
    navigationView.setNavigationItemSelectedListener(
        item -> {
          showSnackbar(item.getTitle());
          return false;
        });
    applyAlpha = view.findViewById(R.id.apply_alpha_button);
    alphaValue = view.findViewById(R.id.alpha_value);
    card1 = view.findViewById(R.id.card1);
    card2 = view.findViewById(R.id.card2);
    card3 = view.findViewById(R.id.card3);
    card4 = view.findViewById(R.id.card4);
    card5 = view.findViewById(R.id.card5);
    card6 = view.findViewById(R.id.card6);
    card7 = view.findViewById(R.id.card7);
    card8 = view.findViewById(R.id.card8);
    view.<MaterialButton>findViewById(R.id.delete_all).setOnClickListener(view1 -> {
      spe.putString(PREFERENCES, "").commit();
          savedConfigurations.setText("");
    });
    view.<MaterialButton>findViewById(R.id.delete_last).setOnClickListener(view1 -> {
      String string = sp.getString(PREFERENCES, "");
      String substring = string.substring(0, ordinalIndexOf(string, "\n", 2, true) + 1);
      spe.putString(PREFERENCES, substring).commit();
        savedConfigurations.setText(substring);
    });
      applyAlpha.setOnClickListener(view1 -> {
      Editable text = alphaValue.getText();
      if(text != null) {
        int value = Integer.parseInt(text.toString());
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = view1.getContext().getTheme();
        theme.resolveAttribute(R.attr.colorSecondaryVariant, typedValue, true);
        @ColorInt int color = typedValue.data;
        ColorStateList secondaryVariantColor = ColorStateList.valueOf(color).withAlpha(value);
        card1.setCardBackgroundColor(secondaryVariantColor);
        card2.setCardBackgroundColor(secondaryVariantColor);
        card3.setCardBackgroundColor(secondaryVariantColor);
        card4.setCardBackgroundColor(secondaryVariantColor);
        card5.setCardBackgroundColor(secondaryVariantColor);
        card6.setCardBackgroundColor(secondaryVariantColor);
        card7.setCardBackgroundColor(secondaryVariantColor);
        card8.setCardBackgroundColor(secondaryVariantColor);
      }
    });
    fab.setOnClickListener(v -> {
      Editable text = alphaValue.getText();
      int value = 0;
      if(text != null) {
        String a = text.toString();
        if (!a.equals("")) {
          value = Integer.parseInt(text.toString());
        }
      }
      Context context = view.getContext();
      String result = "primary: " + Integer.toHexString(getColorFromAttr(context, R.attr.colorPrimary)) + ", secondary: " + Integer.toHexString(getColorFromAttr(context, R.attr.colorSecondary)) + ", variant: " + Integer.toHexString(getColorFromAttr(context, R.attr.colorSecondaryVariant)) + ", Alpha: " + value + "\n";
      showSnackbar("Configuration saved: " + result);
      String previous = sp.getString(PREFERENCES, "");
      String newString = previous + result;
      spe.putString(PREFERENCES, newString).commit();
      savedConfigurations.setText(newString);
    });
    /*Button centerButton = view.findViewById(R.id.center);
    Button endButton = view.findViewById(R.id.end);
    ToggleButton attachToggle = view.findViewById(R.id.attach_toggle);
    attachToggle.setChecked(fab.getVisibility() == View.VISIBLE);
    centerButton.setOnClickListener(
        v -> {
          bar.setFabAlignmentModeAndReplaceMenu(
              BottomAppBar.FAB_ALIGNMENT_MODE_CENTER, R.menu.demo_primary);
        });
    endButton.setOnClickListener(
        v -> {
          bar.setFabAlignmentModeAndReplaceMenu(
              BottomAppBar.FAB_ALIGNMENT_MODE_END, R.menu.demo_primary_alternate);
        });
    attachToggle.setOnCheckedChangeListener(
        (buttonView, isChecked) -> {
          if (isChecked) {
            fab.show();
          } else {
            fab.hide();
          }
        });

    ToggleButton barScrollToggle = view.findViewById(R.id.bar_scroll_toggle);
    barScrollToggle.setChecked(bar.getHideOnScroll());
    barScrollToggle.setOnCheckedChangeListener(
        (buttonView, isChecked) -> bar.setHideOnScroll(isChecked));

    ToggleButton fabAnimToggle = view.findViewById(R.id.fab_animation_mode_toggle);
    fabAnimToggle.setOnCheckedChangeListener(
        (buttonView, isChecked) ->
            bar.setFabAnimationMode(
                isChecked
                    ? BottomAppBar.FAB_ANIMATION_MODE_SLIDE
                    : BottomAppBar.FAB_ANIMATION_MODE_SCALE));
*/
    setUpBottomAppBarShapeAppearance();

    return view;
  }

  @ColorInt public int getColorFromAttr(Context context, int attr){
    TypedValue typedValue = new TypedValue();
    Resources.Theme theme = context.getTheme();
    theme.resolveAttribute(attr, typedValue, true);
    return typedValue.data;
  }
  @Override
  public boolean onBackPressed() {
    if (bottomDrawerBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
      bottomDrawerBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
      return true;
    }
    return false;
  }

  @Override
  public boolean shouldShowDefaultDemoActionBar() {
    return false;
  }

  private void setUpBottomAppBarShapeAppearance() {
    ShapeAppearanceModel fabShapeAppearanceModel = fab.getShapeAppearanceModel();
    boolean cutCornersFab =
        fabShapeAppearanceModel.getBottomLeftCorner() instanceof CutCornerTreatment
            && fabShapeAppearanceModel.getBottomRightCorner() instanceof CutCornerTreatment;

    BottomAppBarTopEdgeTreatment topEdge =
        cutCornersFab
            ? new BottomAppBarCutCornersTopEdge(
                bar.getFabCradleMargin(),
                bar.getFabCradleRoundedCornerRadius(),
                bar.getCradleVerticalOffset())
            : new BottomAppBarTopEdgeTreatment(
                bar.getFabCradleMargin(),
                bar.getFabCradleRoundedCornerRadius(),
                bar.getCradleVerticalOffset());

    MaterialShapeDrawable babBackground = (MaterialShapeDrawable) bar.getBackground();
    babBackground.setShapeAppearanceModel(
        babBackground.getShapeAppearanceModel().toBuilder().setTopEdge(topEdge).build());
  }

  protected void setUpBottomDrawer(View view) {
    View bottomDrawer = coordinatorLayout.findViewById(R.id.bottom_drawer);
    bottomDrawerBehavior = BottomSheetBehavior.from(bottomDrawer);
    bottomDrawerBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    bar.setNavigationOnClickListener(
        v -> bottomDrawerBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED));
    bar.setNavigationIcon(R.drawable.ic_drawer_menu_24px);
    bar.replaceMenu(R.menu.demo_primary);
  }

  private void showSnackbar(CharSequence text) {
    Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG)
        .setAnchorView(fab.getVisibility() == View.VISIBLE ? fab : bar)
        .show();
  }
}
