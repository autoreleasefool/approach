package ca.josephroque.bowlingcompanion.leagues

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import ca.josephroque.bowlingcompanion.App
import ca.josephroque.bowlingcompanion.R
import ca.josephroque.bowlingcompanion.bowlers.Bowler
import ca.josephroque.bowlingcompanion.common.Android
import ca.josephroque.bowlingcompanion.common.ThousandsTextWatcher
import ca.josephroque.bowlingcompanion.utils.BCError
import ca.josephroque.bowlingcompanion.utils.Color
import ca.josephroque.bowlingcompanion.utils.safeLet
import kotlinx.android.synthetic.main.dialog_league.*
import kotlinx.android.synthetic.main.dialog_league.view.*
import kotlinx.coroutines.experimental.launch


/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Dialog to create a new league.
 */
class LeagueDialog : DialogFragment() {

    companion object {
        /** Logging identifier. */
        private const val TAG = "LeagueDialog"

        /** Identifier for the [Bowler] that will own the [League]. */
        private const val ARG_BOWLER = "${TAG}_bowler"

        /** Identifier for the [League] to be edited. */
        private const val ARG_LEAGUE = "${TAG}_league"

        /** Identifier for if the league is an event or not. */
        private const val ARG_IS_EVENT = "${TAG}_is_event"

        /**
         * Create a new instance of the [LeagueDialog].
         *
         * @param bowler bowler that will own the league
         * @param league a [League] to edit, or null to create a new league
         * @param isEvent true to create an event, false to create a league
         */
        fun newInstance(bowler: Bowler, league: League?, isEvent: Boolean): LeagueDialog {
            val dialog = LeagueDialog()
            val args = Bundle()
            args.putParcelable(ARG_BOWLER, bowler)
            args.putBoolean(ARG_IS_EVENT, isEvent)
            league?.let { args.putParcelable(ARG_LEAGUE, league) }
            dialog.arguments = args
            return dialog
        }
    }

    /** Owner of the league. */
    private var bowler: Bowler? = null

    /** League to be edited, or null if a new league is to be created. */
    private var league: League? = null

    /** True if the league should be an event, false otherwise. */
    private var isEvent: Boolean = false

    /** Interaction handler. */
    private var listener: OnLeagueDialogInteractionListener? = null

    /** View OnClickListener. */
    private var onClickListener: View.OnClickListener? = View.OnClickListener {
        val clicked = it ?: return@OnClickListener
        when (clicked.id) {
            R.id.radio_event -> {
                isEvent = true
                layout_additional_games?.visibility = View.GONE
                layout_highlights?.visibility = View.GONE
                setImeOptions()
            }
            R.id.radio_league -> {
                isEvent = false
                layout_additional_games?.visibility = View.VISIBLE
                layout_highlights?.visibility = View.VISIBLE
                setImeOptions()
            }
            R.id.btn_delete -> {
                safeLet(context, league) { context, league ->
                    AlertDialog.Builder(context)
                            .setTitle(String.format(context.resources.getString(R.string.query_delete_item), league.name))
                            .setMessage(R.string.dialog_delete_item_message)
                            .setPositiveButton(R.string.delete, { _, _ ->
                                listener?.onDeleteLeague(league)
                                dismiss()
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show()
                }
            }
        }
    }

    /** @Override */
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        bowler = savedInstanceState?.getParcelable(ARG_BOWLER) ?: arguments?.getParcelable(ARG_BOWLER)
        league = savedInstanceState?.getParcelable(ARG_LEAGUE) ?: arguments?.getParcelable(ARG_LEAGUE)
        isEvent = savedInstanceState?.getBoolean(ARG_IS_EVENT) ?: arguments?.getBoolean(ARG_IS_EVENT) ?: false

        val rootView = inflater.inflate(R.layout.dialog_league, container, false)

        setupToolbar(rootView)
        setupLeagueTypeInput(rootView)
        setupNameInput(rootView)
        setupAdditionalGamesInput(rootView)

        return rootView
    }

    /**
     * Set up title, style, and listeners for toolbar.
     *
     * @param rootView the root view
     */
    private fun setupToolbar(rootView: View) {
        if (league == null) {
            rootView.toolbar_league.setTitle(R.string.new_league)
        } else {
            rootView.toolbar_league.setTitle(R.string.edit_league)
        }

        rootView.btn_delete.setOnClickListener(onClickListener)
        rootView.toolbar_league.apply {
            inflateMenu(R.menu.menu_dialog_league)
            menu.findItem(R.id.action_save).isEnabled = league?.name?.isNotEmpty() == true
            setNavigationIcon(R.drawable.ic_close_white_24dp)
            setNavigationOnClickListener {
                dismiss()
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_save -> {
                        saveLeague()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    /**
     * Set up listeners for league type radio buttons.
     *
     * @param rootView the root view
     */
    private fun setupLeagueTypeInput(rootView: View) {
        rootView.radio_league.setOnClickListener(onClickListener)
        rootView.radio_event.setOnClickListener(onClickListener)

        rootView.radio_league.isChecked = !isEvent
        rootView.radio_event.isChecked = isEvent
    }

    /**
     * Set up listeners for name input view.
     *
     * @param rootView the root view
     */
    private fun setupNameInput(rootView: View) {
        rootView.input_name.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButton()
            }
        })
    }

    /**
     * Set up listeners for additional games input views.
     *
     * @param rootView the root view
     */
    private fun setupAdditionalGamesInput(rootView: View) {
        val additionalGames = rootView.input_additional_games
        val textWatcherGames = object : ThousandsTextWatcher(",", ".") {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                updateSaveButton()
            }
        }
        additionalGames.addTextChangedListener(textWatcherGames)

        val additionalPinfall = rootView.input_additional_pinfall
        val textWatcherPinfall = object : ThousandsTextWatcher(",", ".") {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                updateSaveButton()
            }
        }
        additionalPinfall.addTextChangedListener(textWatcherPinfall)

        rootView.checkbox_additional_games.setOnCheckedChangeListener { _, isChecked ->
            layout_additional_games_details.visibility = if (isChecked) View.VISIBLE else View.GONE
            setImeOptions()
        }
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context as? OnLeagueDialogInteractionListener ?: throw RuntimeException(context!!.toString() + " must implement OnLeagueDialogInteractionListener")
        listener = context
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        listener = null
        onClickListener = null
    }

    /** @Override */
    override fun onResume() {
        super.onResume()

        // Requesting input focus and showing keyboard
        input_name.requestFocus()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(input_name, InputMethodManager.SHOW_IMPLICIT)

        league?.let {
            layout_delete_league.visibility = View.VISIBLE
            layout_new_league_event.visibility = View.GONE
            input_number_of_games.visibility = View.GONE

            if (it.isEvent) {
                layout_additional_games?.visibility = View.GONE
                layout_highlights?.visibility = View.GONE
            } else {
                layout_highlights?.visibility = View.VISIBLE
                if (it.additionalPinfall > 0 || it.additionalGames > 0) {
                    checkbox_additional_games?.isChecked = true
                    layout_additional_games_details.visibility = View.VISIBLE
                    input_additional_games.setText(it.additionalGames.toString())
                    input_additional_pinfall.setText(it.additionalPinfall.toString())
                } else {
                    checkbox_additional_games?.isChecked = false
                    layout_additional_games_details.visibility = View.GONE
                    input_additional_pinfall.setText("")
                    input_additional_games.setText("")
                }
            }

            resetInputs(it)
        }

        input_name.setSelection(input_name.text.length)
        setImeOptions()
        updateSaveButton()
    }

    /** @Override */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(ARG_BOWLER, bowler)
        outState.putParcelable(ARG_LEAGUE, league)
        outState.putBoolean(ARG_IS_EVENT, isEvent)
    }

    /** @Override */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    /**
     * Clean up dialog before calling super.
     */
    override fun dismiss() {
        App.hideSoftKeyBoard(activity!!)
        activity?.supportFragmentManager?.popBackStack()
        super.dismiss()
    }

    /**
     * Checks if the league can be saved or not.
     */
    private fun canSave(): Boolean {
        val name = input_name.text.toString()
        val hasAdditional = checkbox_additional_games.isChecked
        val additionalPinfall = input_additional_pinfall.text.toString()
        val additionalGames = input_additional_games.text.toString()

        return (isEvent && name.isNotEmpty()) ||
                (!isEvent && name.isNotEmpty() && ((hasAdditional && additionalPinfall.isNotEmpty() && additionalGames.isNotEmpty()) || !hasAdditional))
    }

    /**
     * Reset user input values to the league values.
     *
     * @param league the league to reset values to
     */
    private fun resetInputs(league: League) {
        input_name.setText(league.name)
        input_number_of_games.setText(league.gamesPerSeries.toString())
        checkbox_additional_games.isChecked = league.additionalPinfall > 0 || league.additionalGames > 0
        input_game_highlight.setText(league.gameHighlight.toString())
        input_series_highlight.setText(league.seriesHighlight.toString())
        input_additional_pinfall.setText(league.additionalPinfall.toString())
        input_additional_games.setText(league.additionalGames.toString())
    }

    /**
     * Adjust IME options for text fields based on state of the league being created.
     */
    private fun setImeOptions() {
        input_name.imeOptions = if (league == null || !isEvent)
            EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_NEXT
        else
            EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_DONE

        input_number_of_games.imeOptions = if (!isEvent)
            EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_NEXT
        else
            EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_DONE

        input_series_highlight.imeOptions = if (!isEvent && checkbox_additional_games.isChecked)
            EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_NEXT
        else
            EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_DONE

        val activity = activity ?: return
        val focusedField = view?.findFocus() as? EditText ?: return

        App.hideSoftKeyBoard(activity)
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.restartInput(focusedField)
        App.showSoftKeyBoard(activity)
    }

    /**
     * Update save button state based on if the league can be saved or not.
     */
    private fun updateSaveButton() {
        val saveButton = toolbar_league?.menu?.findItem(R.id.action_save)
        if (canSave()) {
            saveButton?.isEnabled = true
            saveButton?.icon?.alpha = Color.ALPHA_ENABLED
        } else {
            saveButton?.isEnabled = false
            saveButton?.icon?.alpha = Color.ALPHA_DISABLED
        }
    }

    /**
     * Save the current league. Show errors if there are any.
     */
    private fun saveLeague() {
        val bowler = bowler ?: return

        launch(Android) {
            this@LeagueDialog.context?.let {
                val oldName = league?.name ?: ""
                val oldAdditionalPinfall = league?.additionalPinfall ?: 0
                val oldAdditionalGames = league?.additionalGames ?: 0
                val oldGameHighlight = league?.gameHighlight ?: 0
                val oldSeriesHighlight = league?.seriesHighlight ?: 0

                if (canSave()) {
                    val name = input_name.text.toString()
                    val numberOfGamesStr = input_number_of_games.text.toString()
                    val numberOfGames: Int

                    val hasAdditional = checkbox_additional_games.isChecked
                    val additionalPinfallStr = input_additional_pinfall.text.toString().replace(",", "")
                    val additionalGamesStr = input_additional_games.text.toString().replace(",", "")
                    var additionalPinfall = 0
                    var additionalGames = 0

                    val gameHighlightStr = input_game_highlight.text.toString().replace(",", "")
                    val seriesHighlightStr = input_series_highlight.text.toString().replace(",", "")
                    var gameHighlight = 0
                    var seriesHighlight = 0

                    try {
                        numberOfGames = numberOfGamesStr.toInt()
                    } catch (ex: NumberFormatException) {
                        BCError(
                                it.resources.getString(R.string.error_saving_league),
                                it.resources.getString(R.string.error_league_number_of_games_invalid),
                                BCError.Severity.Error
                        ).show(it)
                        return@launch
                    }

                    if (hasAdditional && additionalGamesStr.isNotEmpty() && additionalPinfallStr.isNotEmpty()) {
                        try {
                            additionalPinfall = additionalPinfallStr.toInt()
                            additionalGames = additionalGamesStr.toInt()
                        } catch (ex: NumberFormatException) {
                            BCError(
                                    it.resources.getString(R.string.error_saving_league),
                                    it.resources.getString(R.string.error_league_additional_info_invalid),
                                    BCError.Severity.Error
                            ).show(it)
                            return@launch
                        }
                    }

                    if (!isEvent && gameHighlightStr.isNotEmpty() && seriesHighlightStr.isNotEmpty()) {
                        try {
                            gameHighlight = gameHighlightStr.toInt()
                            seriesHighlight = seriesHighlightStr.toInt()
                        } catch (ex: NumberFormatException) {
                            BCError(
                                    it.resources.getString(R.string.error_saving_league),
                                    it.resources.getString(R.string.error_league_highlight_invalid),
                                    BCError.Severity.Error
                            ).show(it)
                            return@launch
                        }
                    }

                    val newLeague = league ?: League(bowler,-1, name, 0.0, isEvent, numberOfGames, additionalPinfall, additionalGames, gameHighlight, seriesHighlight)
                    newLeague.name = name
                    newLeague.additionalPinfall = additionalPinfall
                    newLeague.additionalGames = additionalGames
                    newLeague.gameHighlight = gameHighlight
                    newLeague.seriesHighlight = seriesHighlight

                    val error = newLeague.save(it).await()
                    if (error != null) {
                        error.show(it)
                        newLeague.name = oldName
                        newLeague.additionalPinfall = oldAdditionalPinfall
                        newLeague.additionalGames = oldAdditionalGames
                        newLeague.additionalPinfall = oldGameHighlight
                        newLeague.additionalGames = oldSeriesHighlight


                        resetInputs(newLeague)
                        if (league != null) input_name.setText(oldName)
                        setImeOptions()
                    } else {
                        dismiss()
                        listener?.onFinishLeague(newLeague)
                    }
                }
            }
        }
    }

    /**
     * Handles interactions with the dialog.
     */
    interface OnLeagueDialogInteractionListener {

        /**
         * Indicates when the user has finished editing the [League]
         *
         * @param league the finished [League]
         */
        fun onFinishLeague(league: League)

        /**
         * Indicates the user wishes to delete the [League].
         *
         * @param league the deleted [League]
         */
        fun onDeleteLeague(league: League)
    }
}
