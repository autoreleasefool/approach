import ComposableArchitecture
import EquatablePackageLibrary
import ErrorsFeature
import ExtensionsPackageLibrary
import ModelsLibrary
import ModelsViewsLibrary
import ResourcePickerLibrary
import StatisticsLibrary
import StatisticsWidgetsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsPackageLibrary
import ViewsLibrary

@ViewAction(for: StatisticsWidgetConfigurationEditor.self)
public struct StatisticsWidgetConfigurationEditorView<Header: View, Footer: View>: View {
	@Bindable public var store: StoreOf<StatisticsWidgetConfigurationEditor>
	let header: () -> Header
	let footer: () -> Footer

	public init(
		store: StoreOf<StatisticsWidgetConfigurationEditor>,
		@ViewBuilder header: @escaping () -> Header,
		@ViewBuilder footer: @escaping () -> Footer
	) {
		self.store = store
		self.header = header
		self.footer = footer
	}

	public init(
		store: StoreOf<StatisticsWidgetConfigurationEditor>,
		@ViewBuilder header: @escaping () -> Header
	) where Footer == EmptyView {
		self.init(store: store, header: header, footer: { EmptyView() })
	}
	public init(
		store: StoreOf<StatisticsWidgetConfigurationEditor>,
		@ViewBuilder footer: @escaping () -> Footer
	) where Header == EmptyView {
		self.init(store: store, header: { EmptyView() }, footer: footer)
	}

	public var body: some View {
		Form {
			header()
			content
			footer()
		}
		.onFirstAppear { send(.didFirstAppear) }
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.bowlerPicker($store.scope(state: \.destination?.bowlerPicker, action: \.internal.destination.bowlerPicker))
		.leaguePicker($store.scope(state: \.destination?.leaguePicker, action: \.internal.destination.leaguePicker))
		.statisticPicker($store.scope(state: \.destination?.statisticPicker, action: \.internal.destination.statisticPicker))
	}

	@ViewBuilder private var content: some View {
		if store.isLoadingSources {
			ListProgressView()
		} else {
			timelineSection
			sourcePickersSection
			statisticSection
		}
	}

	@ViewBuilder private var timelineSection: some View {
		Section {
			Picker(
				Strings.Widget.Builder.timeline,
				selection: $store.timeline
			) {
				ForEach(StatisticsWidget.Timeline.allCases) {
					Text(String(describing: $0)).tag($0)
				}
			}
			.pickerStyle(.navigationLink)
		} footer: {
			Text(Strings.Widget.Builder.Timeline.description)
		}
	}

	@ViewBuilder private var sourcePickersSection: some View {
		Section {
			if store.isBowlerEditable {
				Button { send(.didTapBowler) } label: {
					LabeledContent(Strings.Bowler.title, value: store.bowler?.name ?? Strings.none)
				}
				.buttonStyle(.navigation)
			} else {
				LabeledContent(Strings.Bowler.title, value: store.bowler?.name ?? Strings.none)
			}

			if store.isShowingLeaguePicker {
				Button { send(.didTapLeague) } label: {
					LabeledContent(Strings.League.title, value: store.league?.name ?? Strings.none)
				}
				.buttonStyle(.navigation)
			}
		} footer: {
			Text(Strings.Widget.Builder.Filter.description)
		}
	}

	@ViewBuilder private var statisticSection: some View {
		Section {
			Button { send(.didTapStatistic) } label: {
				Text(store.statistic)
			}
			.buttonStyle(.navigation)
		}
	}
}

@MainActor extension View {
	fileprivate func bowlerPicker(
		_ store: Binding<StoreOf<ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>>?>
	) -> some View {
		navigationDestination(item: store) { (store: StoreOf<ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>>) in
			ResourcePickerView(store: store) { bowler in
				Bowler.View(bowler)
			}
		}
	}

	fileprivate func leaguePicker(
		_ store: Binding<StoreOf<ResourcePicker<League.Summary, Bowler.ID>>?>
	) -> some View {
		navigationDestination(item: store) { (store: StoreOf<ResourcePicker<League.Summary, Bowler.ID>>) in
			ResourcePickerView(store: store) { league in
				Text(league.name)
			}
		}
	}

	fileprivate func statisticPicker(_ store: Binding<StoreOf<StatisticPicker>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<StatisticPicker>) in
			StatisticPickerView(store: store)
		}
	}
}
