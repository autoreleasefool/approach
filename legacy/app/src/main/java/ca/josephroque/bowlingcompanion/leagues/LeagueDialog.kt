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
        @Suppress("unused")
        private const val TAG = "LeagueDialog"

        private const val ARG_BOWLER = "${TAG}_bowler"
        private const val ARG_LEAGUE = "${TAG}_league"
        private const val ARG_IS_EVENT = "${TAG}_is_event"

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

    private var bowler: Bowler? = null
    private var league: League? = null
    private var isEvent: Boolean = false
    private var delegate: LeagueDialogDelegate? = null

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
                            }
                            .setNegativeButton(R.string.cancel, null)
                            .show()
                }
            }
        }
    }

    // MARK: Lifecycle functions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog)
    }

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

        league?.let { resetInputs(it, rootView) }
        setupToolbar(rootView)
        setupLeagueTypeInput(rootView)
        setupNameInput(rootView)
        setupAdditionalGamesInput(rootView)

        return rootView
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        val parent = parentFragment as? LeagueDialogDelegate ?: throw RuntimeException("${parentFragment!!} must implement LeagueDialogDelegate")
        delegate = parent
    }

    override fun onDetach() {
        super.onDetach()
        delegate = null
        onClickListener = null
    }

    override fun onStart() {
        super.onStart()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

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
        }

        nameInput.text?.let { nameInput.setSelection(it.length) }
        setImeOptions()
        setLeagueOptionsVisible()
        updateSaveButton()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun dismiss() {
        App.hideSoftKeyBoard(activity!!)
        activity?.supportFragmentManager?.popBackStack()
        super.dismiss()
    }

    // MARK: Private functions

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

    private fun setupLeagueTypeInput(rootView: View) {
        rootView.radio_league.setOnClickListener(onClickListener)
        rootView.radio_event.setOnClickListener(onClickListener)

        rootView.radio_league.isChecked = !isEvent
        rootView.radio_event.isChecked = isEvent
    }

    private fun setupNameInput(rootView: View) {
        rootView.input_name.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateSaveButton()
            }
        })
    }

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

    private fun canSave(): Boolean {
        val name = nameInput.text.toString()
        val hasAdditional = additionalGamesCheckbox.isChecked
        val additionalGames = additionalGamesInput.text.toString()
        val additionalPinfall = additionalPinfallInput.text.toString()

        return (isEvent && name.isNotEmpty()) ||
                (!isEvent && name.isNotEmpty() && ((hasAdditional && additionalPinfall.isNotEmpty() && additionalGames.isNotEmpty()) || !hasAdditional))
    }

    private fun resetInputs(league: League, rootView: View? = null) {
        val nameInput = rootView?.input_name ?: this.nameInput
        val numberOfGamesInput = rootView?.input_number_of_games ?: this.numberOfGamesInput
        val additionalGamesCheckbox = rootView?.checkbox_additional_games ?: this.additionalGamesCheckbox
        val gameHighlightInput = rootView?.input_game_highlight ?: this.gameHighlightInput
        val seriesHighlightInput = rootView?.input_series_highlight ?: this.seriesHighlightInput
        val additionalGamesInput = rootView?.input_additional_games ?: this.additionalGamesInput
        val additionalPinfallInput = rootView?.input_additional_pinfall ?: this.additionalPinfallInput

        nameInput.setText(league.name)
        numberOfGamesInput.setText(league.gamesPerSeries.toString())
        additionalGamesCheckbox.isChecked = league.additionalPinfall > 0 || league.additionalGames > 0
        gameHighlightInput.setText(league.gameHighlight.toString())
        seriesHighlightInput.setText(league.seriesHighlight.toString())
        additionalGamesInput.setText(league.additionalGames.toString())
        additionalPinfallInput.setText(league.additionalPinfall.toString())
    }

    private fun setImeOptions() {
        nameInput.imeOptions = if (league == null || !isEvent) {
            EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_NEXT
        } else {
            EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_DONE
        }

        numberOfGamesInput.imeOptions = if (!isEvent) {
            EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_NEXT
        } else {
            EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_DONE
        }

        seriesHighlightInput.imeOptions = if (!isEvent && additionalGamesCheckbox.isChecked) {
            EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_NEXT
        } else {
            EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_ACTION_DONE
        }

        val activity = activity ?: return
        val focusedField = view?.findFocus() as? EditText ?: return

        App.hideSoftKeyBoard(activity)
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.restartInput(focusedField)
        App.showSoftKeyBoard(activity)
    }

    private fun setLeagueOptionsVisible() {
        if (isEvent) {
            additionalGamesLayout?.visibility = View.GONE
            highlightsLayout?.visibility = View.GONE
        } else {
            additionalGamesLayout?.visibility = View.VISIBLE
            highlightsLayout?.visibility = View.VISIBLE
        }
    }

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

    // MARK: LeagueDialogDelegate

    interface LeagueDialogDelegate {
        fun onFinishLeague(league: League)
        fun onDeleteLeague(league: League)
    }
}
