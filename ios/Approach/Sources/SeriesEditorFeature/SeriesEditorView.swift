import AssetsLibrary
import ComposableArchitecture
import FeatureActionLibrary
import FormFeature
import MapKit
import ModelsLibrary
import ModelsViewsLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary

public struct SeriesEditorView: View {
	let store: StoreOf<SeriesEditor>
	typealias SeriesEditorViewStore = ViewStore<ViewState, SeriesEditor.Action.ViewAction>

	struct ViewState: Equatable {
		@BindingViewState var date: Date
		@BindingViewState var numberOfGames: Int
		@BindingViewState var preBowl: Series.PreBowl
		@BindingViewState var excludeFromStatistics: Series.ExcludeFromStatistics
		@BindingViewState var coordinate: CoordinateRegion
		let location: Alley.Summary?

		let excludeLeagueFromStatistics: League.ExcludeFromStatistics

		let hasAlleysEnabled: Bool

		let isEditing: Bool
		let isDismissDisabled: Bool
	}

	public init(store: StoreOf<SeriesEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			FormView(store: store.scope(state: \.form, action: /SeriesEditor.Action.InternalAction.form)) {
				Section(Strings.Editor.Fields.Details.title) {
					Stepper(
						Strings.Series.Editor.Fields.numberOfGames(viewStore.numberOfGames),
						value: viewStore.$numberOfGames,
						in: League.NUMBER_OF_GAMES_RANGE
					)
					.disabled(viewStore.isEditing)

					DatePicker(
						Strings.Series.Properties.date,
						selection: viewStore.$date,
						displayedComponents: [.date]
					)
					.datePickerStyle(.graphical)
				}

				locationSection(viewStore)

				Section {
					Picker(
						Strings.Series.Editor.Fields.PreBowl.label,
						selection: viewStore.$preBowl
					) {
						ForEach(Series.PreBowl.allCases) {
							Text(String(describing: $0)).tag($0)
						}
					}
				} header: {
					Text(Strings.Series.Editor.Fields.PreBowl.title)
				} footer: {
					Text(Strings.Series.Editor.Fields.PreBowl.help)
				}

				Section {
					Picker(
						Strings.Series.Editor.Fields.ExcludeFromStatistics.label,
						selection: viewStore.$excludeFromStatistics
					) {
						ForEach(Series.ExcludeFromStatistics.allCases) {
							Text(String(describing: $0)).tag($0)
						}
					}.disabled(viewStore.preBowl == .preBowl || viewStore.excludeLeagueFromStatistics == .exclude)
				} header: {
					Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.title)
				} footer: {
					excludeFromStatisticsHelp(viewStore)
				}
			}
			.interactiveDismissDisabled(viewStore.isDismissDisabled)
		})
		.navigationDestination(
			store: store.scope(state: \.$alleyPicker, action: { .internal(.alleyPicker($0)) })
		) { store in
			ResourcePickerView(store: store) { alley in
				Alley.View(alley)
			}
		}
	}

	@ViewBuilder private func locationSection(_ viewStore: SeriesEditorViewStore) -> some View {
		if viewStore.hasAlleysEnabled {
			Section {
				Button { viewStore.send(.didTapAlley) } label: {
					LabeledContent(
						Strings.Series.Properties.alley,
						value: viewStore.location?.name ?? Strings.none
					)
				}
				.buttonStyle(.navigation)

				if let location = viewStore.location?.location {
					Map(
						coordinateRegion: viewStore.$coordinate.mkCoordinateRegion,
						interactionModes: [],
						annotationItems: [location]
					) { place in
						MapMarker(coordinate: place.coordinate.mapCoordinate, tint: Asset.Colors.Action.default.swiftUIColor)
					}
					.frame(maxWidth: .infinity)
					.frame(height: 125)
					.padding(0)
					.listRowInsets(EdgeInsets())
				}
			} header: {
				Text(Strings.Series.Editor.Fields.Alley.title)
			} footer: {
				Text(Strings.Series.Editor.Fields.Alley.help)
			}
			.listRowSeparator(.hidden)
		}
	}

	@ViewBuilder private func excludeFromStatisticsHelp(_ viewStore: SeriesEditorViewStore) -> some View {
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

extension SeriesEditorView.ViewState {
	init(store: BindingViewStore<SeriesEditor.State>) {
		self._date = store.$date
		self._numberOfGames = store.$numberOfGames
		self._preBowl = store.$preBowl
		self._excludeFromStatistics = store.$excludeFromStatistics
		self._coordinate = store.$coordinate
		self.location = store.location

		self.excludeLeagueFromStatistics = store.league.excludeFromStatistics

		self.hasAlleysEnabled = store.hasAlleysEnabled
		self.isDismissDisabled = store.alleyPicker != nil

		switch store._form.value {
		case .create: self.isEditing = false
		case .edit: self.isEditing = true
		}
	}
}

extension Series.PreBowl: CustomStringConvertible {
	public var description: String {
		switch self {
		case .preBowl: return Strings.Series.Properties.PreBowl.preBowl
		case .regular: return Strings.Series.Properties.PreBowl.regular
		}
	}
}

extension Series.ExcludeFromStatistics: CustomStringConvertible {
	public var description: String {
		switch self {
		case .exclude: return Strings.Series.Properties.ExcludeFromStatistics.exclude
		case .include: return Strings.Series.Properties.ExcludeFromStatistics.include
		}
	}
}
