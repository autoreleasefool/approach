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

@ViewAction(for: SeriesEditor.self)
public struct SeriesEditorView: View {
	@Bindable public var store: StoreOf<SeriesEditor>

	public init(store: StoreOf<SeriesEditor>) {
		self.store = store
	}

	public var body: some View {
		FormView(store: store.scope(state: \.form, action: \.internal.form)) {
			detailsSection

			if store.isManualSeriesEnabled && !store.isEditing {
				manualSection
			}

			locationSection
			preBowlSection
			statisticsSection
		}
		.interactiveDismissDisabled(store.isDismissDisabled)
		.onAppear { send(.onAppear) }
		.navigationDestination(
			item: $store.scope(state: \.alleyPicker, action: \.internal.alleyPicker)
		) { store in
			ResourcePickerView(store: store) { alley in
				Alley.View(alley)
			}
		}
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

			if store.isPreBowlFormEnabled && store.preBowl == .preBowl {
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
		switch store.league.excludeFromStatistics {
		case .exclude:
			Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.excludedWhenLeagueExcluded)
				.foregroundColor(Asset.Colors.Warning.default)
		case .include:
			switch (store.preBowl, store.isUsingPreBowl) {
			case (.preBowl, false):
				Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.excludedWhenPreBowl)
					.foregroundColor(Asset.Colors.Warning.default)
			case (.preBowl, true), (.regular, _):
				Text(Strings.Series.Editor.Fields.ExcludeFromStatistics.help)
			}
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
