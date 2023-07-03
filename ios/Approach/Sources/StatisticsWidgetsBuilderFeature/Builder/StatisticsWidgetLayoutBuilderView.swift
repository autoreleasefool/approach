import AssetsLibrary
import ComposableArchitecture
import ModelsLibrary
import ReorderingLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsRepositoryInterface
import StatisticsWidgetsLibrary
import StringsLibrary
import SwiftUI
import ViewsLibrary

public struct StatisticsWidgetLayoutBuilderView: View {
	let store: StoreOf<StatisticsWidgetLayoutBuilder>

	struct ViewState: Equatable {
		let isDeleting: Bool
		let widgetData: [StatisticsWidget.ID: Statistics.ChartContent]

		init(state: StatisticsWidgetLayoutBuilder.State) {
			self.isDeleting = state.isDeleting
			self.widgetData = state.widgetData
		}
	}

	enum ViewAction {
		case didObserveData
		case didTapAddNew
		case didTapDeleteButton
		case didTapCancelDeleteButton
		case didTapWidget(id: StatisticsWidget.ID)
	}

	public init(store: StoreOf<StatisticsWidgetLayoutBuilder>) {
		self.store = store
	}

	public var body: some View {
		WithViewStore(store, observe: ViewState.init, send: StatisticsWidgetLayoutBuilder.Action.init) { viewStore in
			ScrollView {
				LazyVGrid(
					columns: [.init(spacing: .largeSpacing), .init(spacing: .largeSpacing)],
					spacing: .largeSpacing
				) {
					ReorderableView(
						store: store.scope(state: \.reordering, action: { .internal(.reordering($0)) })
					) { widget in
						SquareWidget(
							configuration: widget,
							chartContent: viewStore.widgetData[widget.id],
							onPress: nil,
							onDelete: viewStore.isDeleting ? { viewStore.send(.didTapWidget(id: widget.id)) } : nil
						)
					}
				}
				.padding(.horizontal, .largeSpacing)

				Text(Strings.Widget.LayoutBuilder.instructions)
					.font(.caption)
					.multilineTextAlignment(.center)
					.padding(.largeSpacing)
			}
			.toolbar {
				if viewStore.isDeleting {
					ToolbarItem(placement: .navigationBarTrailing) {
						Button(Strings.Action.done) { viewStore.send(.didTapCancelDeleteButton) }
					}
				} else {
					ToolbarItem(placement: .navigationBarTrailing) {
						AddButton { viewStore.send(.didTapAddNew) }
					}

					ToolbarItem(placement: .navigationBarLeading) {
						DeleteButton { viewStore.send(.didTapDeleteButton) }
					}
				}
			}
			.navigationTitle(Strings.Widget.LayoutBuilder.title)
			.task { await viewStore.send(.didObserveData).finish() }
		}
		.sheet(store: store.scope(state: \.$editor, action: { .internal(.editor($0)) })) { store in
			NavigationStack {
				StatisticsWidgetEditorView(store: store)
			}
		}
	}
}

extension StatisticsWidgetLayoutBuilder.Action {
	init(action: StatisticsWidgetLayoutBuilderView.ViewAction) {
		switch action {
		case .didTapDeleteButton:
			self = .view(.didTapDeleteButton)
		case .didTapCancelDeleteButton:
			self = .view(.didTapCancelDeleteButton)
		case let .didTapWidget(id):
			self = .view(.didTapWidget(id: id))
		case .didObserveData:
			self = .view(.didObserveData)
		case .didTapAddNew:
			self = .view(.didTapAddNew)
		}
	}
}

public struct SquareWidget: View {
	let configuration: StatisticsWidget.Configuration
	let chartContent: Statistics.ChartContent?
	let onPress: (() -> Void)?
	let onDelete: (() -> Void)?

	init(
		configuration: StatisticsWidget.Configuration,
		chartContent: Statistics.ChartContent?,
		onPress: (() -> Void)?,
		onDelete: (() -> Void)? = nil
	) {
		self.configuration = configuration
		self.chartContent = chartContent
		self.onPress = onPress
		self.onDelete = onDelete
	}

	public var body: some View {
		Group {
			if let onPress {
				Button(action: onPress) {
					StatisticsWidget.Widget(configuration: configuration, chartContent: chartContent)
						.aspectRatio(1, contentMode: .fit)
						.cornerRadius(.standardRadius)
				}
				.buttonStyle(TappableElement())
			} else {
				StatisticsWidget.Widget(configuration: configuration, chartContent: chartContent)
					.aspectRatio(1, contentMode: .fit)
					.cornerRadius(.standardRadius)
			}
		}
		.overlay(alignment: .topTrailing) {
			if let onDelete {
				Button { onDelete() } label: {
					ZStack(alignment: .center) {
						Circle()
							.fill(Asset.Colors.Destructive.default.swiftUIColor)
							.frame(width: .smallerIcon, height: .smallerIcon)

						Image(systemName: "xmark")
							.resizable()
							.scaledToFit()
							.frame(width: .tinyIcon, height: .tinyIcon)
							.foregroundColor(.white)
					}
					.padding(.top, (.standardSpacing + .smallSpacing) * -1)
					.padding(.trailing, (.standardSpacing + .smallSpacing) * -1)
					.padding(.standardSpacing)
				}
			}
		}
	}
}
