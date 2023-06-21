import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import FormLibrary
import ModelsLibrary
import ModelsViewsLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary

public struct SeriesEditorView: View {
	let store: StoreOf<SeriesEditor>

	struct ViewState: Equatable {
		let date: Date
		let numberOfGames: Int
		let preBowl: Series.PreBowl
		let excludeFromStatistics: Series.ExcludeFromStatistics
		let location: Alley.Summary?

		let hasSetNumberOfGames: Bool
		let excludeLeagueFromStatistics: League.ExcludeFromStatistics

		let hasAlleysEnabled: Bool

		let isEditing: Bool
		let isDismissDisabled: Bool

		init(state: SeriesEditor.State) {
			self.date = state.date
			self.numberOfGames = state.numberOfGames
			self.preBowl = state.preBowl
			self.excludeFromStatistics = state.excludeFromStatistics
			self.location = state.location

			self.hasSetNumberOfGames = state.league.numberOfGames != nil
			self.excludeLeagueFromStatistics = state.league.excludeFromStatistics

			self.hasAlleysEnabled = state.hasAlleysEnabled
			self.isDismissDisabled = state.alleyPicker != nil

			switch state._form.value {
			case .create: self.isEditing = false
			case .edit: self.isEditing = true
			}
		}
	}

	enum ViewAction {
		case didTapAlley
		case didChangeDate(Date)
		case didChangeNumberOfGames(Int)
		case didChangePreBowl(Series.PreBowl)
		case didChangeExcludeFromStatistics(Series.ExcludeFromStatistics)
	}

	public init(store: StoreOf<SeriesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: SeriesEditor.Action.init) { viewStore in
			FormView(store: store.scope(state: \.form, action: /SeriesEditor.Action.InternalAction.form)) {
				Section(Strings.Editor.Fields.Details.title) {
					Stepper(
						Strings.Series.Editor.Fields.numberOfGames(viewStore.numberOfGames),
						value: viewStore.binding(get: \.numberOfGames, send: ViewAction.didChangeNumberOfGames),
						in: League.NUMBER_OF_GAMES_RANGE
					)
					.disabled(viewStore.hasSetNumberOfGames || viewStore.isEditing)

					DatePicker(
						Strings.Series.Properties.date,
						selection: viewStore.binding(get: \.date, send: ViewAction.didChangeDate),
						displayedComponents: [.date]
					)
					.datePickerStyle(.graphical)
				}

				if viewStore.hasAlleysEnabled {
					Section(Strings.Series.Properties.alley) {
						Button { viewStore.send(.didTapAlley) } label: {
							LabeledContent(
								Strings.Series.Properties.alley,
								value: viewStore.location?.name ?? Strings.none
							)
						}
						.buttonStyle(.navigation)
					}
				}

				Section {
					Toggle(
						Strings.Series.Editor.Fields.PreBowl.label,
						isOn: viewStore.binding(
							get: { $0.preBowl == .preBowl },
							send: { ViewAction.didChangePreBowl($0 ? .preBowl : .regular) }
						)
					)
				} header: {
					Text(Strings.Series.Editor.Fields.PreBowl.title)
				} footer: {
					Text(Strings.Series.Editor.Fields.PreBowl.help)
				}

				Section {
					Toggle(
						Strings.Series.Editor.Fields.ExcludeFromStatistics.label,
						isOn: viewStore.binding(
							get: {
								$0.excludeLeagueFromStatistics == .exclude ||
								$0.preBowl == .preBowl ||
								$0.excludeFromStatistics == .exclude
							},
							send: { ViewAction.didChangeExcludeFromStatistics($0 ? .exclude : .include) }
						)
					).disabled(viewStore.preBowl == .preBowl || viewStore.excludeLeagueFromStatistics == .exclude)
				} header: {
					Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.title)
				} footer: {
					excludeFromStatisticsHelp(viewStore)
				}
			}
			.navigationDestination(
				store: store.scope(state: \.$alleyPicker, action: { .internal(.alleyPicker($0)) })
			) { store in
				ResourcePickerView(store: store) { alley in
					Alley.View(alley: alley)
				}
			}
			.interactiveDismissDisabled(viewStore.isDismissDisabled)
		}
	}

	@ViewBuilder private func excludeFromStatisticsHelp(_ viewStore: ViewStore<ViewState, ViewAction>) -> some View {
		switch viewStore.excludeLeagueFromStatistics {
		case .exclude:
			Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.excludedWhenLeagueExcluded)
				.foregroundColor(Asset.Colors.Warning.default)
		case .include:
			switch viewStore.preBowl {
			case .preBowl:
				Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.excludedWhenPreBowl)
					.foregroundColor(Asset.Colors.Warning.default)
			case .regular:
				Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.help)
			}
		}
	}
}

extension SeriesEditor.Action {
	init(action: SeriesEditorView.ViewAction) {
		switch action {
		case .didTapAlley:
			self = .view(.didTapAlley)
		case let .didChangeDate(date):
			self = .view(.didChangeDate(date))
		case let .didChangePreBowl(preBowl):
			self = .view(.didChangePreBowl(preBowl))
		case let .didChangeNumberOfGames(numberOfGames):
			self = .view(.didChangeNumberOfGames(numberOfGames))
		case let .didChangeExcludeFromStatistics(exclude):
			self = .view(.didChangeExcludeFromStatistics(exclude))
		}
	}
}
