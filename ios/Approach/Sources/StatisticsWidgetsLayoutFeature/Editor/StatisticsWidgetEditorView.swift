import ComposableArchitecture
import EquatableLibrary
import ErrorsFeature
import ExtensionsLibrary
import ModelsLibrary
import ModelsViewsLibrary
import ResourcePickerLibrary
import StatisticsLibrary
import StatisticsWidgetsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import TipsLibrary
import ViewsLibrary

public struct StatisticsWidgetEditorView: View {
	let store: StoreOf<StatisticsWidgetEditor>

	struct ViewState: Equatable {
		let source: StatisticsWidget.Source?
		let sources: StatisticsWidget.Sources?
		@BindingViewState var timeline: StatisticsWidget.Timeline
		let statistic: String

		let selectedBowlerName: String?
		let selectedLeagueName: String?

		let isShowingLeaguePicker: Bool
		let isLoadingSources: Bool
		let isLoadingPreview: Bool
		let isSaveable: Bool
		let isBowlerEditable: Bool

		let widgetConfiguration: StatisticsWidget.Configuration?
		let widgetPreviewData: Statistics.ChartContent?

		let isShowingTapThroughTip: Bool
	}

	public init(store: StoreOf<StatisticsWidgetEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: { .view($0) }, content: { viewStore in
			Form {
				if viewStore.isLoadingSources {
					ListProgressView()
				} else {
					Section {
						Picker(
							Strings.Widget.Builder.timeline,
							selection: viewStore.$timeline
						) {
							ForEach(StatisticsWidget.Timeline.allCases) {
								Text(String(describing: $0)).tag($0)
							}
						}
						.pickerStyle(.navigationLink)
					} footer: {
						Text(Strings.Widget.Builder.Timeline.description)
					}

					Section {
						if viewStore.isBowlerEditable {
							Button { viewStore.send(.didTapBowler) } label: {
								LabeledContent(Strings.Bowler.title, value: viewStore.selectedBowlerName ?? Strings.none)
							}
							.buttonStyle(.navigation)
						} else {
							LabeledContent(Strings.Bowler.title, value: viewStore.selectedBowlerName ?? Strings.none)
						}

						if viewStore.isShowingLeaguePicker {
							Button { viewStore.send(.didTapLeague) } label: {
								LabeledContent(Strings.League.title, value: viewStore.selectedLeagueName ?? Strings.none)
							}
							.buttonStyle(.navigation)
						}
					} footer: {
						Text(Strings.Widget.Builder.Filter.description)
					}

					Section {
						Button { viewStore.send(.didTapStatistic) } label: {
							Text(viewStore.statistic)
						}
						.buttonStyle(.navigation)
					}

					if let configuration = viewStore.widgetConfiguration, let chartContent = viewStore.widgetPreviewData {
						Section(Strings.Widget.Builder.preview) {
							Button { viewStore.send(.didTapWidget) } label: {
								StatisticsWidget.Widget(configuration: configuration, chartContent: chartContent)
									.aspectRatio(2, contentMode: .fit)
							}
							.buttonStyle(TappableElement())
						}
						.listRowInsets(EdgeInsets())
					} else if viewStore.isLoadingPreview {
						ProgressView()
					}

					if viewStore.isShowingTapThroughTip {
						Section {
							BasicTipView(tip: .tapThroughStatisticTip) {
								viewStore.send(.didTapDismissTapThroughTip, animation: .default)
							}
						}
					}
				}
			}
			.navigationTitle(Strings.Widget.Builder.title)
			.toolbar {
				ToolbarItem(placement: .navigationBarTrailing) {
					Button(Strings.Action.save) { viewStore.send(.didTapSaveButton) }
						.disabled(!viewStore.isSaveable)
				}
			}
			.onFirstAppear { viewStore.send(.didFirstAppear) }
			.onAppear { viewStore.send(.onAppear) }
		})
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.bowlerPicker(store.scope(state: \.$destination.bowlerPicker, action: \.internal.destination.bowlerPicker))
		.leaguePicker(store.scope(state: \.$destination.leaguePicker, action: \.internal.destination.leaguePicker))
		.statisticPicker(store.scope(state: \.$destination.statisticPicker, action: \.internal.destination.statisticPicker))
		.help(store.scope(state: \.$destination.help, action: \.internal.destination.help))
	}
}

@MainActor extension View {
	fileprivate func bowlerPicker(
		_ store: PresentationStoreOf<ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>>
	) -> some View {
		navigationDestination(store: store) { (store: StoreOf<ResourcePicker<Bowler.Summary, AlwaysEqual<Void>>>) in
			ResourcePickerView(store: store) { bowler in
				Bowler.View(bowler)
			}
		}
	}

	fileprivate func leaguePicker(
		_ store: PresentationStoreOf<ResourcePicker<League.Summary, Bowler.ID>>
	) -> some View {
		navigationDestination(store: store) { (store: StoreOf<ResourcePicker<League.Summary, Bowler.ID>>) in
			ResourcePickerView(store: store) { league in
				Text(league.name)
			}
		}
	}

	fileprivate func statisticPicker(_ store: PresentationStoreOf<StatisticPicker>) -> some View {
		navigationDestination(store: store) { (store: StoreOf<StatisticPicker>) in
			StatisticPickerView(store: store)
		}
	}

	fileprivate func help(_ store: PresentationStoreOf<StatisticsWidgetHelp>) -> some View {
		sheet(store: store) { (store: StoreOf<StatisticsWidgetHelp>) in
			NavigationStack {
				StatisticsWidgetHelpView(store: store)
			}
		}
	}
}

extension StatisticsWidgetEditorView.ViewState {
	init(store: BindingViewStore<StatisticsWidgetEditor.State>) {
		self._timeline = store.$timeline
		self.statistic = store.statistic
		self.source = store.source
		self.sources = store.sources
		self.selectedBowlerName = store.bowler?.name
		self.selectedLeagueName = store.league?.name
		self.isShowingLeaguePicker = selectedBowlerName != nil
		self.isLoadingSources = store.isLoadingSources
		self.isLoadingPreview = store.isLoadingPreview
		self.isSaveable = store.source != nil
		self.isBowlerEditable = store.isBowlerEditable
		self.widgetConfiguration = store.configuration
		self.widgetPreviewData = store.widgetPreviewData
		self.isShowingTapThroughTip = store.isShowingTapThroughTip
	}
}
