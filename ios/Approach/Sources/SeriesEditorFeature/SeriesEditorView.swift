import AssetsLibrary
import ComposableArchitecture
import EquatablePackageLibrary
import FeatureActionLibrary
import FormFeature
import MapKit
import ModelsLibrary
import ModelsViewsLibrary
import ResourcePickerLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary

@ViewAction(for: SeriesEditor.self)
public struct SeriesEditorView: View {
	@Bindable public var store: StoreOf<SeriesEditor>

	public init(store: StoreOf<SeriesEditor>) {
		self.store = store
	}

	public var body: some View {
		FormView(store: store.scope(state: \.form, action: \.internal.form)) {
			if !store.hasLeagueChanged {
				detailsSection

				if !store.isEditing {
					manualSection
				}

				locationSection
				preBowlSection
				statisticsSection
			}

			if store.isEditing && store.isMovingSeriesBetweenLeaguesEnabled {
				leagueSection
			}
		}
		.interactiveDismissDisabled(store.isDismissDisabled)
		.onAppear { send(.onAppear) }
		.destinations($store)
	}

	private var detailsSection: some View {
		Section(Strings.Editor.Fields.Details.title) {
			Stepper(
				Strings.Series.Editor.Fields.numberOfGames(store.numberOfGames),
				value: $store.numberOfGames,
				in: League.NUMBER_OF_GAMES_RANGE
			)
			.disabled(store.isEditing)

			DatePicker(
				Strings.Series.Properties.date,
				selection: $store.date,
				displayedComponents: [.date]
			)
			.datePickerStyle(.graphical)
		}
	}

	private var manualSection: some View {
		Section {
			Toggle(
				Strings.Series.Editor.Fields.Manual.setScoresManually,
				isOn: $store.isCreatingManualSeries.animation()
			)

			if store.isCreatingManualSeries {
				ForEach(store.scope(state: \.manualScores, action: \.internal.manualSeriesGame), id: \.state.id) {
					ManualSeriesGameEditorView(store: $0)
				}
			}
		} header: {
			Text(Strings.Series.Editor.Fields.Manual.title)
		} footer: {
			Text(Strings.Series.Editor.Fields.Manual.footer)
		}
	}

	private var locationSection: some View {
		Section {
			Button { send(.didTapAlley) } label: {
				LabeledContent(
					Strings.Series.Properties.alley,
					value: store.location?.name ?? Strings.none
				)
			}
			.buttonStyle(.navigation)

			if let location = store.location?.location {
				Map(position: $store.mapPosition, interactionModes: []) {
					Marker(location.title, coordinate: location.coordinate.mapCoordinate)
						.tint(Asset.Colors.Action.default.swiftUIColor)
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

	private var preBowlSection: some View {
		Section {
			Picker(
				Strings.Series.Editor.Fields.PreBowl.label,
				selection: $store.preBowl.animation()
			) {
				ForEach(Series.PreBowl.allCases) {
					Text(String(describing: $0)).tag($0)
				}
			}

			if store.preBowl == .preBowl {
				Toggle(
					Strings.Series.Editor.Fields.PreBowl.usePreBowl,
					isOn: $store.isUsingPreBowl.animation()
				)

				if store.isUsingPreBowl {
					DatePicker(
						Strings.Series.Editor.Fields.PreBowl.date,
						selection: $store.appliedDate,
						displayedComponents: [.date]
					)
					.datePickerStyle(.graphical)
				}
			}
		} header: {
			Text(Strings.Series.Editor.Fields.PreBowl.title)
		} footer: {
			Text(Strings.Series.Editor.Fields.PreBowl.help)
		}
	}

	private var statisticsSection: some View {
		Section {
			Picker(
				Strings.Series.Editor.Fields.ExcludeFromStatistics.label,
				selection: $store.excludeFromStatistics
			) {
				ForEach(Series.ExcludeFromStatistics.allCases) {
					Text(String(describing: $0)).tag($0)
				}
			}
			.disabled(store.isExcludeFromStatisticsToggleEnabled)
		} header: {
			Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.title)
		} footer: {
			excludeFromStatisticsHelp
		}
	}

	@ViewBuilder private var excludeFromStatisticsHelp: some View {
		switch store.initialLeague.excludeFromStatistics {
		case .exclude:
			Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.excludedWhenLeagueExcluded)
				.foregroundStyle(Asset.Colors.Warning.default)
		case .include:
			switch (store.preBowl, store.isUsingPreBowl) {
			case (.preBowl, false):
				Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.excludedWhenPreBowl)
					.foregroundStyle(Asset.Colors.Warning.default)
			case (.preBowl, true), (.regular, _):
				Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.help)
			}
		}
	}

	@ViewBuilder private var leagueSection: some View {
		if store.hasLeagueChanged {
			Section {
				Text(Strings.Series.Editor.Fields.League.otherFieldsDisabled)
			}
		}

		Section {
			VStack(alignment: .leading, spacing: .smallSpacing) {
				Text(Strings.Series.Editor.Fields.League.label)
			}

			Button {
				send(.didTapLeague)
			} label: {
				LabeledContent(Strings.League.title, value: store.currentLeague.name)
			}
			.buttonStyle(.navigation)
		} header: {
			Text(Strings.Series.Editor.Fields.League.title)
		} footer: {
			VStack(alignment: .leading, spacing: .unitSpacing) {
				Text(Strings.Series.Editor.Fields.League.help)
					.frame(maxWidth: .infinity, alignment: .leading)

				HStack(alignment: .center, spacing: .smallSpacing) {
					Image(systemName: "exclamationmark.triangle")
						.resizable()
						.frame(width: .tinyIcon, height: .tinyIcon)
						.foregroundStyle(Asset.Colors.Warning.default)

					Text(Strings.Series.Editor.Fields.League.statistics)
						.frame(maxWidth: .infinity, alignment: .leading)
				}
			}
		}
		.listRowSeparator(.hidden)
	}
}

extension Series.PreBowl: CustomStringConvertible {
	public var description: String {
		switch self {
		case .preBowl: Strings.Series.Properties.PreBowl.preBowl
		case .regular: Strings.Series.Properties.PreBowl.regular
		}
	}
}

extension Series.ExcludeFromStatistics: CustomStringConvertible {
	public var description: String {
		switch self {
		case .exclude: Strings.Series.Properties.ExcludeFromStatistics.exclude
		case .include: Strings.Series.Properties.ExcludeFromStatistics.include
		}
	}
}

// MARK: - Destinations

extension View {
	fileprivate func destinations(_ store: Bindable<StoreOf<SeriesEditor>>) -> some View {
		self
			.alleyPicker(store.scope(state: \.destination?.alleyPicker, action: \.internal.destination.alleyPicker))
			.leaguePicker(store.scope(state: \.destination?.leaguePicker, action: \.internal.destination.leaguePicker))
	}

	fileprivate func alleyPicker(
		_ store: Binding<StoreOf<ResourcePicker<Alley.Summary, AlwaysEqual<Void>>>?>
	) -> some View {
		navigationDestination(item: store) {
			ResourcePickerView(store: $0) { alley in
				Alley.View(alley)
			}
		}
	}

	fileprivate func leaguePicker(
		_ store: Binding<StoreOf<ResourcePicker<League.Summary, Bowler.ID>>?>
	) -> some View {
		navigationDestination(item: store) {
			ResourcePickerView(store: $0) { league in
				Text(league.name)
			}
		}
	}
}

// MARK: - Preview

#Preview {
	NavigationStack {
		SeriesEditorView(
			store: Store(
				initialState: SeriesEditor.State(
					value: .edit(.placeholder),
					inLeague: .placeholder
				),
				reducer: { SeriesEditor() }
			)
		)
	}
}
