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

@ViewAction(for: LeagueEditor.self)
public struct LeagueEditorView: View {
	@Perception.Bindable public var store: StoreOf<LeagueEditor>

	public init(store: StoreOf<LeagueEditor>) {
		self.store = store
	}

	public var body: some View {
		WithPerceptionTracking {
			FormView(store: store.scope(state: \.form, action: \.internal.form)) {
				detailsSection
				recurrenceSection
				locationSection
				statisticsSection
				gamesSection
				additionalPinfallSection
			}
			.interactiveDismissDisabled(store.isDismissDisabled)
			.onAppear { send(.onAppear) }
			.navigationDestinationWrapper(
				item: $store.scope(state: \.alleyPicker, action: \.internal.alleyPicker)
			) { store in
				ResourcePickerView(store: store) { alley in
					Alley.View(alley)
				}
			}
		}
	}

	private var detailsSection: some View {
		Section(Strings.Editor.Fields.Details.title) {
			TextField(
				Strings.Editor.Fields.Details.name,
				text: $store.name
			)
		}
	}

	@ViewBuilder private var recurrenceSection: some View {
		if !store.isEditing {
			Section {
				Picker(
					Strings.League.Properties.recurrence,
					selection: $store.recurrence
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

	@ViewBuilder private var locationSection: some View {
		if store.shouldShowLocationSection {
			Section {
				Button { send(.didTapAlley) } label: {
					LabeledContent(
						Strings.League.Properties.alley,
						value: store.location?.name ?? Strings.none
					)
				}
				.buttonStyle(.navigation)

				if let location = store.location?.location {
					Map(
						coordinateRegion: $store.coordinate.mkCoordinateRegion,
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

	private var statisticsSection: some View {
		Section {
			Picker(
				Strings.League.Editor.Fields.ExcludeFromStatistics.label,
				selection: $store.excludeFromStatistics
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

	@ViewBuilder private var gamesSection: some View {
		if !store.isEditing {
			Section {
				Picker(
					Strings.League.Properties.numberOfGames,
					selection: $store.gamesPerSeries
				) {
					ForEach(LeagueEditor.GamesPerSeries.allCases) {
						Text(String(describing: $0)).tag($0)
					}
				}
				.disabled(store.recurrence == .once)

				if store.gamesPerSeries == .static {
					Stepper(
						"\(store.defaultNumberOfGames)",
						value: $store.defaultNumberOfGames,
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

	private var additionalPinfallSection: some View {
		Section {
			Toggle(
				Strings.League.Editor.Fields.AdditionalPinfall.title,
				isOn: $store.hasAdditionalPinfall
			)
			.toggleStyle(SwitchToggleStyle())

			if store.hasAdditionalPinfall {
				TextField(
					Strings.League.Properties.additionalPinfall,
					text: $store.additionalPinfall
				)
				.keyboardType(.numberPad)

				TextField(
					Strings.League.Properties.additionalGames,
					text: $store.additionalGames
				)
				.keyboardType(.numberPad)
			}
		} footer: {
			Text(Strings.League.Editor.Fields.AdditionalPinfall.help)
		}
	}
}
