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
import ViewsLibrary

public struct LeagueEditorView: View {
	let store: StoreOf<LeagueEditor>
	typealias LeagueEditorViewStore = ViewStore<ViewState, LeagueEditor.Action.ViewAction>

	struct ViewState: Equatable {
		@BindingViewState var name: String
		@BindingViewState var recurrence: League.Recurrence
		@BindingViewState var numberOfGames: Int
		@BindingViewState var additionalPinfall: String
		@BindingViewState var additionalGames: String
		@BindingViewState var excludeFromStatistics: League.ExcludeFromStatistics

		@BindingViewState var gamesPerSeries: LeagueEditor.GamesPerSeries
		@BindingViewState var hasAdditionalPinfall: Bool

		@BindingViewState var coordinate: CoordinateRegion

		let shouldShowLocationSection: Bool
		let location: Alley.Summary?

		let isEditing: Bool
		let isDismissDisabled: Bool
	}

	public init(store: StoreOf<LeagueEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			FormView(store: store.scope(state: \.form, action: \.internal.form)) {
				detailsSection(viewStore)
				recurrenceSection(viewStore)
				locationSection(viewStore)
				statisticsSection(viewStore)
				gamesSection(viewStore)
				additionalPinfallSection(viewStore)
			}
			.interactiveDismissDisabled(viewStore.isDismissDisabled)
			.onAppear { viewStore.send(.onAppear) }
		})
		.navigationDestination(
			store: store.scope(state: \.$alleyPicker, action: \.internal.alleyPicker)
		) { store in
			ResourcePickerView(store: store) { alley in
				Alley.View(alley)
			}
		}
	}

	private func detailsSection(_ viewStore: LeagueEditorViewStore) -> some View {
		Section(Strings.Editor.Fields.Details.title) {
			TextField(
				Strings.Editor.Fields.Details.name,
				text: viewStore.$name
			)
		}
	}

	@ViewBuilder private func recurrenceSection(_ viewStore: LeagueEditorViewStore) -> some View {
		if !viewStore.isEditing {
			Section {
				Picker(
					Strings.League.Properties.recurrence,
					selection: viewStore.$recurrence
				) {
					ForEach(League.Recurrence.allCases) {
						Text(String(describing: $0)).tag($0)
					}
				}
			} footer: {
				Text(Strings.League.Editor.Fields.Recurrence.help(League.Recurrence.repeating, League.Recurrence.once))
			}
		}
	}

	@ViewBuilder private func locationSection(_ viewStore: LeagueEditorViewStore) -> some View {
		if viewStore.shouldShowLocationSection {
			Section {
				Button { viewStore.send(.didTapAlley) } label: {
					LabeledContent(
						Strings.League.Properties.alley,
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
				Text(Strings.League.Editor.Fields.Alley.title)
			} footer: {
				Text(Strings.League.Editor.Fields.Alley.help)
			}
			.listRowSeparator(.hidden)
		}
	}

	private func statisticsSection(_ viewStore: LeagueEditorViewStore) -> some View {
		Section {
			Picker(
				Strings.League.Editor.Fields.ExcludeFromStatistics.label,
				selection: viewStore.$excludeFromStatistics
			) {
				ForEach(League.ExcludeFromStatistics.allCases) {
					Text(String(describing: $0)).tag($0)
				}
			}
		} header: {
			Text(Strings.League.Editor.Fields.ExcludeFromStatistics.title)
		} footer: {
			Text(Strings.League.Editor.Fields.ExcludeFromStatistics.help)
		}
	}

	@ViewBuilder private func gamesSection(_ viewStore: LeagueEditorViewStore) -> some View {
		if !viewStore.isEditing {
			Section {
				Picker(
					Strings.League.Properties.numberOfGames,
					selection: viewStore.$gamesPerSeries
				) {
					ForEach(LeagueEditor.GamesPerSeries.allCases) {
						Text(String(describing: $0)).tag($0)
					}
				}
				.disabled(viewStore.recurrence == .once)

				if viewStore.gamesPerSeries == .static {
					Stepper(
						"\(viewStore.numberOfGames)",
						value: viewStore.$numberOfGames,
						in: League.NUMBER_OF_GAMES_RANGE
					)
				}
			} footer: {
				Text(
					Strings.League.Editor.Fields.NumberOfGames.help(
						LeagueEditor.GamesPerSeries.static,
						LeagueEditor.GamesPerSeries.dynamic
					)
				)
			}
		}
	}

	private func additionalPinfallSection(_ viewStore: LeagueEditorViewStore) -> some View {
		Section {
			Toggle(
				Strings.League.Editor.Fields.AdditionalPinfall.title,
				isOn: viewStore.$hasAdditionalPinfall
			)
			.toggleStyle(SwitchToggleStyle())

			if viewStore.hasAdditionalPinfall {
				TextField(
					Strings.League.Properties.additionalPinfall,
					text: viewStore.$additionalPinfall
				)
				.keyboardType(.numberPad)

				TextField(
					Strings.League.Properties.additionalGames,
					text: viewStore.$additionalGames
				)
				.keyboardType(.numberPad)
			}
		} footer: {
			Text(Strings.League.Editor.Fields.AdditionalPinfall.help)
		}
	}
}

extension LeagueEditorView.ViewState {
	init(store: BindingViewStore<LeagueEditor.State>) {
		self._name = store.$name
		self._recurrence = store.$recurrence
		self._numberOfGames = store.$numberOfGames
		self._additionalGames = store.$additionalGames
		self._additionalPinfall = store.$additionalPinfall
		self._excludeFromStatistics = store.$excludeFromStatistics
		self._coordinate = store.$coordinate

		self._gamesPerSeries = store.$gamesPerSeries
		self._hasAdditionalPinfall = store.$hasAdditionalPinfall

		self.isDismissDisabled = store.alleyPicker != nil
		self.location = store.location
		self.shouldShowLocationSection = store.shouldShowLocationSection

		switch store._form.value {
		case .create: self.isEditing = false
		case .edit: self.isEditing = true
		}
	}
}
