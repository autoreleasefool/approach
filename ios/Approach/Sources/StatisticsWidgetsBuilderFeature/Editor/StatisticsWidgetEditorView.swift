import ComposableArchitecture
import ModelsLibrary
import ResourcePickerLibrary
import StatisticsLibrary
import StatisticsWidgetsLibrary
import StringsLibrary
import SwiftUI
import SwiftUIExtensionsLibrary
import ViewsLibrary

public struct StatisticsWidgetEditorView: View {
	let store: StoreOf<StatisticsWidgetEditor>

	struct ViewState: Equatable {
		let source: StatisticsWidget.Source?
		let sources: StatisticsWidget.Sources?
		let timeline: StatisticsWidget.Timeline
		let statistic: StatisticsWidget.Statistic

		let selectedBowlerName: String?
		let selectedLeagueName: String?

		let isShowingLeaguePicker: Bool
		let isLoadingSources: Bool
		let isLoadingPreview: Bool
		let isSaveable: Bool

		let widgetConfiguration: StatisticsWidget.Configuration?
		let widgetPreviewData: Statistics.ChartContent?

		init(state: StatisticsWidgetEditor.State) {
			self.source = state.source
			self.sources = state.sources
			self.timeline = state.timeline
			self.statistic = state.statistic
			self.selectedBowlerName = state.bowler?.name
			self.selectedLeagueName = state.league?.name
			self.isShowingLeaguePicker = selectedBowlerName != nil
			self.isLoadingSources = state.isLoadingSources
			self.isLoadingPreview = state.isLoadingPreview
			self.isSaveable = state.source != nil
			self.widgetConfiguration = state.configuration
			self.widgetPreviewData = state.widgetPreviewData
		}
	}

	enum ViewAction {
		case onAppear
		case didTapBowler
		case didTapLeague
		case didTapSaveButton
		case didChangeTimeline(StatisticsWidget.Timeline)
		case didChangeStatistic(StatisticsWidget.Statistic)
	}

	public init(store: StoreOf<StatisticsWidgetEditor>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: StatisticsWidgetEditor.Action.init) { viewStore in
			Form {
				if viewStore.isLoadingSources {
					ListProgressView()
				} else {
					Section {
						Picker(
							Strings.Widget.Builder.timeline,
							selection: viewStore.binding(get: \.timeline, send: ViewAction.didChangeTimeline)
						) {
							ForEach(StatisticsWidget.Timeline.allCases) {
								Text(String(describing: $0)).tag($0)
							}
						}
						.pickerStyle(.navigationLink)
					}

					Section {
						Button { viewStore.send(.didTapBowler) } label: {
							LabeledContent(Strings.Bowler.title, value: viewStore.selectedBowlerName ?? Strings.none)
						}
						.buttonStyle(.navigation)

						if viewStore.isShowingLeaguePicker {
							Button { viewStore.send(.didTapLeague) } label: {
								LabeledContent(Strings.League.title, value: viewStore.selectedLeagueName ?? Strings.none)
							}
							.buttonStyle(.navigation)
						}
					}

					Section {
						Picker(
							Strings.Widget.Builder.statistic,
							selection: viewStore.binding(get: \.statistic, send: ViewAction.didChangeStatistic)
						) {
							ForEach(StatisticsWidget.Statistic.allCases) {
								Text(String(describing: $0)).tag($0)
							}
						}
						.pickerStyle(.navigationLink)
					}

					if let configuration = viewStore.widgetConfiguration, let chartContent = viewStore.widgetPreviewData {
						Section(Strings.Widget.Builder.preview) {
							StatisticsWidget.Widget(configuration: configuration, chartContent: chartContent)
								.aspectRatio(2, contentMode: .fit)
						}
						.listRowInsets(EdgeInsets())
					} else if viewStore.isLoadingPreview {
						ProgressView()
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
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /StatisticsWidgetEditor.Destination.State.bowlerPicker,
			action: StatisticsWidgetEditor.Destination.Action.bowlerPicker
		) { store in
			ResourcePickerView(store: store) { bowler in
				Text(bowler.name)
			}
		}
		.navigationDestination(
			store: store.scope(state: \.$destination, action: { .internal(.destination($0)) }),
			state: /StatisticsWidgetEditor.Destination.State.leaguePicker,
			action: StatisticsWidgetEditor.Destination.Action.leaguePicker
		) { store in
			ResourcePickerView(store: store) { league in
				Text(league.name)
			}
		}
	}
}

extension StatisticsWidgetEditor.Action {
	init(action: StatisticsWidgetEditorView.ViewAction) {
		switch action {
		case .onAppear:
			self = .view(.onAppear)
		case .didTapBowler:
			self = .view(.didTapBowler)
		case .didTapLeague:
			self = .view(.didTapLeague)
		case .didTapSaveButton:
			self = .view(.didTapSaveButton)
		case let .didChangeTimeline(timeline):
			self = .view(.didChangeTimeline(timeline))
		case let .didChangeStatistic(statistic):
			self = .view(.didChangeStatistic(statistic))
		}
	}
}
