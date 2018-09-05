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
import ca.josephroque.bowlingcompanion.common.fragments.BaseDialogFragment
import ca.josephroque.bowlingcompanion.utils.Analytics
import ca.josephroque.bowlingcompanion.utils.BCError
import ca.josephroque.bowlingcompanion.utils.Color
import ca.josephroque.bowlingcompanion.utils.safeLet
import kotlinx.android.synthetic.main.dialog_league.checkbox_additional_games as additionalGamesCheckbox
import kotlinx.android.synthetic.main.dialog_league.input_additional_games as additionalGamesInput
import kotlinx.android.synthetic.main.dialog_league.input_additional_pinfall as additionalPinfallInput
import kotlinx.android.synthetic.main.dialog_league.input_game_highlight as gameHighlightInput
import kotlinx.android.synthetic.main.dialog_league.input_series_highlight as seriesHighlightInput
import kotlinx.android.synthetic.main.dialog_league.input_name as nameInput
import kotlinx.android.synthetic.main.dialog_league.input_number_of_games as numberOfGamesInput
import kotlinx.android.synthetic.main.dialog_league.layout_additional_games_details as additionalGamesLayoutDetails
import kotlinx.android.synthetic.main.dialog_league.layout_additional_games as additionalGamesLayout
import kotlinx.android.synthetic.main.dialog_league.layout_delete_league as deleteLeagueLayout
import kotlinx.android.synthetic.main.dialog_league.layout_highlights as highlightsLayout
import kotlinx.android.synthetic.main.dialog_league.layout_new_league_event as newLeagueEventLayout
import kotlinx.android.synthetic.main.dialog_league.toolbar_league as leagueToolbar
import kotlinx.android.synthetic.main.dialog_league.view.*
import kotlinx.coroutines.experimental.launch

/**
 * Copyright (C) 2018 Joseph Roque
 *
 * Dialog to create a new league.
 */
class LeagueDialog : BaseDialogFragment() {

    companion object {
        /** Logging identifier. */
        @Suppress("unused")
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
            dialog.arguments = Bundle().apply {
                putParcelable(ARG_BOWLER, bowler)
                putBoolean(ARG_IS_EVENT, isEvent)
                league?.let { putParcelable(ARG_LEAGUE, league) }
            }
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
    private var delegate: LeagueDialogDelegate? = null

    /** View OnClickListener. */
    private var onClickListener: View.OnClickListener? = View.OnClickListener {
        val clicked = it ?: return@OnClickListener
        when (clicked.id) {
            R.id.radio_event -> {
                isEvent = true
                setLeagueOptionsVisible()
                setImeOptions()
            }
            R.id.radio_league -> {
                isEvent = false
                setLeagueOptionsVisible()
                setImeOptions()
            }
            R.id.btn_delete -> {
                safeLet(context, league) { context, league ->
                    AlertDialog.Builder(context)
                            .setTitle(String.format(context.resources.getString(R.string.query_delete_item), league.name))
                            .setMessage(R.string.dialog_delete_item_message)
                            .setPositiveButton(R.string.delete) { _, _ ->
                                delegate?.onDeleteLeague(league)
                                dismiss()

                                Analytics.trackDeleteLeague()
                            }
                            .setNegativeButton(R.string.cancel, null)
                            .show()
                }
            }
        }
    }

    /** @Override */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog)
    }

    /** @Override */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            bowler = it.getParcelable(ARG_BOWLER)
            league = it.getParcelable(ARG_LEAGUE)
            isEvent = it.getBoolean(ARG_IS_EVENT)
        }

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
            inflateMenu(R.menu.dialog_league)
            menu.findItem(R.id.action_save).isEnabled = league?.name?.isNotEmpty() == true
            setNavigationIcon(R.drawable.ic_dismiss)
            setNavigationOnClickListener {
                dismiss()
            }
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_save -> {
                        saveLeague()
                        true
                    }
                    else -> super.onOptionsItemSelected(it)
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
        rootView.input_name.addTextChangedListener(object : TextWatcher {
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
            additionalGamesLayoutDetails.visibility = if (isChecked) View.VISIBLE else View.GONE
            setImeOptions()
        }
    }

    /** @Override */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parent = parentFragment as? LeagueDialogDelegate ?: throw RuntimeException("${parentFragment!!} must implement LeagueDialogDelegate")
        delegate = parent
    }

    /** @Override */
    override fun onDetach() {
        super.onDetach()
        delegate = null
        onClickListener = null
    }

    /** @Override */
    override fun onStart() {
        super.onStart()
        dialog.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    /** @Override */
    override fun onResume() {
        super.onResume()

        // Requesting input focus and showing keyboard
        nameInput.requestFocus()
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(nameInput, InputMethodManager.SHOW_IMPLICIT)

        league?.let {
            deleteLeagueLayout.visibility = View.VISIBLE
            newLeagueEventLayout.visibility = View.GONE
            numberOfGamesInput.isEnabled = false

            if (it.isEvent) {
                additionalGamesLayout?.visibility = View.GONE
                highlightsLayout?.visibility = View.GONE
            } else {
                highlightsLayout?.visibility = View.VISIBLE
                if (it.additionalPinfall > 0 || it.additionalGames > 0) {
                    additionalGamesCheckbox?.isChecked = true
                    additionalGamesLayoutDetails.visibility = View.VISIBLE
                    additionalGamesInput.setText(it.additionalGames.toString())
                    additionalPinfallInput.setText(it.additionalPinfall.toString())
                } else {
                    additionalGamesCheckbox?.isChecked = false
                    additionalGamesLayoutDetails.visibility = View.GONE
                    additionalGamesInput.setText("")
                    additionalPinfallInput.setText("")
                }
            }

            resetInputs(it)
        }

        nameInput.setSelection(nameInput.text.length)
        setImeOptions()
        setLeagueOptionsVisible()
        updateSaveButton()
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
        val name = nameInput.text.toString()
        val hasAdditional = additionalGamesCheckbox.isChecked
        val additionalGames = additionalGamesInput.text.toString()
        val additionalPinfall = additionalPinfallInput.text.toString()

        return (isEvent && name.isNotEmpty()) ||
                (!isEvent && name.isNotEmpty() && ((hasAdditional && additionalPinfall.isNotEmpty() && additionalGames.isNotEmpty()) || !hasAdditional))
    }

    /**
     * Reset user input values to the league values.
     *
     * @param league the league to reset values to
     */
    private fun resetInputs(league: League) {
        nameInput.setText(league.name)
        numberOfGamesInput.setText(league.gamesPerSeries.toString())
        additionalGamesCheckbox.isChecked = league.additionalPinfall > 0 || league.additionalGames > 0
        gameHighlightInput.setText(league.gameHighlight.toString())
        seriesHighlightInput.setText(league.seriesHighlight.toString())
        additionalGamesInput.setText(league.additionalGames.toString())
        additionalPinfallInput.setText(league.additionalPinfall.toString())
    }

    /**
     * Adjust IME options for text fields based on state of the league being created.
     */
    private fun setImeOptions() {
        nameInput.imeOptions = if (league == null || !isEvent)
            EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_NEXT
        else
            EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_DONE

        numberOfGamesInput.imeOptions = if (!isEvent)
            EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_NEXT
        else
            EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_DONE

        seriesHighlightInput.imeOptions = if (!isEvent && additionalGamesCheckbox.isChecked)
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
     * Set which options are visible for the user to provide details about the league / event
     * they want to create.
     */
    private fun setLeagueOptionsVisible() {
        if (isEvent) {
            additionalGamesLayout?.visibility = View.GONE
            highlightsLayout?.visibility = View.GONE
        } else {
            additionalGamesLayout?.visibility = View.VISIBLE
            highlightsLayout?.visibility = View.VISIBLE
        }
    }

    /**
     * Update save button state based on if the league can be saved or not.
     */
    private fun updateSaveButton() {
        val saveButton = leagueToolbar?.menu?.findItem(R.id.action_save)
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
            this@LeagueDialog.context?.let { context ->
                if (canSave()) {
                    val name = nameInput.text.toString()
                    val numberOfGamesStr = numberOfGamesInput.text.toString()
                    val numberOfGames: Int

                    val hasAdditional = additionalGamesCheckbox.isChecked
                    val additionalGamesStr = additionalGamesInput.text.toString().replace(",", "")
                    val additionalPinfallStr = additionalPinfallInput.text.toString().replace(",", "")
                    var additionalPinfall = 0
                    var additionalGames = 0

                    val gameHighlightStr = gameHighlightInput.text.toString().replace(",", "")
                    val seriesHighlightStr = seriesHighlightInput.text.toString().replace(",", "")
                    var gameHighlight = 0
                    var seriesHighlight = 0

                    try {
                        numberOfGames = numberOfGamesStr.toInt()
                    } catch (ex: NumberFormatException) {
                        BCError(
                                R.string.issue_saving_league,
                                R.string.error_league_number_of_games_invalid,
                                BCError.Severity.Warning
                        ).show(context)
                        return@launch
                    }

                    if (hasAdditional && additionalGamesStr.isNotEmpty() && additionalPinfallStr.isNotEmpty()) {
                        try {
                            additionalPinfall = additionalPinfallStr.toInt()
                            additionalGames = additionalGamesStr.toInt()
                        } catch (ex: NumberFormatException) {
                            BCError(
                                    R.string.issue_saving_league,
                                    R.string.error_league_additional_info_invalid,
                                    BCError.Severity.Warning
                            ).show(context)
                            return@launch
                        }
                    }

                    if (!isEvent && gameHighlightStr.isNotEmpty() && seriesHighlightStr.isNotEmpty()) {
                        try {
                            gameHighlight = gameHighlightStr.toInt()
                            seriesHighlight = seriesHighlightStr.toInt()
                        } catch (ex: NumberFormatException) {
                            BCError(
                                    R.string.issue_saving_league,
                                    R.string.error_league_highlight_invalid,
                                    BCError.Severity.Warning
                            ).show(context)
                            return@launch
                        }
                    }

                    val oldLeague = league
                    val (newLeague, error) = if (oldLeague != null) {
                        League.save(
                                context = context,
                                id = oldLeague.id,
                                bowler = oldLeague.bowler,
                                name = name,
                                average = oldLeague.average,
                                isEvent = oldLeague.isEvent,
                                gamesPerSeries = oldLeague.gamesPerSeries,
                                additionalPinfall = additionalPinfall,
                                additionalGames = additionalGames,
                                gameHighlight = gameHighlight,
                                seriesHighlight = seriesHighlight
                        ).await()
                    } else {
                        League.save(
                                context = context,
                                id = -1,
                                bowler = bowler,
                                name = name,
                                isEvent = isEvent,
                                gamesPerSeries = numberOfGames,
                                additionalPinfall = additionalPinfall,
                                additionalGames = additionalGames,
                                gameHighlight = gameHighlight,
                                seriesHighlight = seriesHighlight
                        ).await()
                    }

                    if (error != null) {
                        error.show(context)

                        league?.let { resetInputs(it) }
                        setImeOptions()
                    } else if (newLeague != null) {
                        dismiss()
                        delegate?.onFinishLeague(newLeague)

                        if (oldLeague == null) {
                            Analytics.trackCreateLeague(isEvent, numberOfGames, hasAdditional)
                        } else {
                            Analytics.trackEditLeague()
                        }
                    }
                }
            }
        }
    }

    /**
     * Handles interactions with the dialog.
     */
    interface LeagueDialogDelegate {

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
