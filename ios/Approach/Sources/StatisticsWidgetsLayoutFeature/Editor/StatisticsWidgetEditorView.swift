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
import TipsLibrary
import ViewsLibrary

@ViewAction(for: StatisticsWidgetEditor.self)
public struct StatisticsWidgetEditorView: View {
	@Bindable public var store: StoreOf<StatisticsWidgetEditor>

	public init(store: StoreOf<StatisticsWidgetEditor>) {
		self.store = store
	}

	public var body: some View {
		Form {
			if store.isLoadingSources {
				ListProgressView()
			} else {
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

				Section {
					Button { send(.didTapStatistic) } label: {
						Text(store.statistic)
					}
					.buttonStyle(.navigation)
				}

				if let configuration = store.configuration, let chartContent = store.widgetPreviewData {
					Section(Strings.Widget.Builder.preview) {
						Button { send(.didTapWidget) } label: {
							StatisticsWidget.Widget(configuration: configuration, chartContent: chartContent)
								.aspectRatio(2, contentMode: .fit)
						}
						.buttonStyle(TappableElement())
					}
					.listRowInsets(EdgeInsets())
				} else if store.isLoadingPreview {
					ProgressView()
				}

				if store.isShowingTapThroughTip {
					Section {
						BasicTipView(tip: .tapThroughStatisticTip) {
							send(.didTapDismissTapThroughTip, animation: .default)
						}
					}
				}
			}
		}
		.navigationTitle(Strings.Widget.Builder.title)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				Button(Strings.Action.save) { send(.didTapSaveButton) }
					.disabled(!store.isSaveable)
			}
		}
		.onFirstAppear { send(.didFirstAppear) }
		.onAppear { send(.onAppear) }
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
		.bowlerPicker($store.scope(state: \.destination?.bowlerPicker, action: \.internal.destination.bowlerPicker))
		.leaguePicker($store.scope(state: \.destination?.leaguePicker, action: \.internal.destination.leaguePicker))
		.statisticPicker($store.scope(state: \.destination?.statisticPicker, action: \.internal.destination.statisticPicker))
		.help($store.scope(state: \.destination?.help, action: \.internal.destination.help))
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

	fileprivate func help(_ store: Binding<StoreOf<StatisticsWidgetHelp>?>) -> some View {
		sheet(item: store) { (store: StoreOf<StatisticsWidgetHelp>) in
			NavigationStack {
				StatisticsWidgetHelpView(store: store)
			}
		}
	}
}
