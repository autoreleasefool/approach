import AnalyticsServiceInterface
import AssetsLibrary
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import ModelsLibrary
import StatisticsChartsLibrary
import StatisticsLibrary
import StatisticsWidgetEditorFeature
import StatisticsWidgetsLibrary
import StatisticsWidgetsRepositoryInterface
import StringsLibrary
import SwiftUI
import ViewsLibrary

@Reducer
public struct StatisticsWidgetSharing: Reducer {
	@ObservableState
	public struct State: Equatable {
		public var editor: StatisticsWidgetConfigurationEditor.State

		public var widgetPreviewData: Statistics.ChartContent?
		public var displayScale: CGFloat = .zero
		public var preferredAppearance: Appearance = .dark

		public var errors: Errors<ErrorID>.State = .init()

		public init(
			source: StatisticsWidget.Source?,
			statistic: String?
		) {
			self.editor = .init(source: source, statistic: statistic ?? Statistics.GameAverage.title)
		}

		var configuration: ShareableStatisticsImage.Configuration? {
			guard let configuration = editor.configuration?.withSubtitle(),
						let chartContent = widgetPreviewData else { return nil }
			return .init(
				widget: configuration,
				chart: chartContent,
				displayScale: displayScale,
				colorScheme: preferredAppearance.colorScheme
			)
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable public enum View {
			case onAppear
			case didUpdateDisplayScale(CGFloat)
			case didUpdateColorScheme(ColorScheme)
		}
		@CasePathable public enum Delegate {
			case imageRendered(UIImage)
		}
		@CasePathable public enum Internal {
			case didLoadChartContent(Result<Statistics.ChartContent, Error>)
			case hideChart

			case editor(StatisticsWidgetConfigurationEditor.Action)
			case errors(Errors<ErrorID>.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	public enum CancelID {
		case imageRenderer
		case loadingPreview
	}

	public enum ErrorID: Hashable {
		case failedToLoadChart
	}

	@Dependency(StatisticsWidgetsRepository.self) var statisticsWidgets

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		CombineReducers {
			BindingReducer()

			Scope(state: \.editor, action: \.internal.editor) {
				StatisticsWidgetConfigurationEditor()
			}

			Reduce<State, Action> { state, action in
				switch action {
				case let .view(viewAction):
					switch viewAction {
					case .onAppear:
						return refreshChart(withConfiguration: state.editor.configuration, state: &state)

					case let .didUpdateDisplayScale(displayScale):
						state.displayScale = displayScale
						return .none

					case let .didUpdateColorScheme(colorScheme):
						switch colorScheme {
						case .dark: state.preferredAppearance = .dark
						case .light: state.preferredAppearance = .light
						@unknown default: state.preferredAppearance = .light
						}
						return .none
					}

				case let .internal(internalAction):
					switch internalAction {
					case .hideChart:
						state.widgetPreviewData = nil
						return .none

					case let .didLoadChartContent(.success(content)):
						state.widgetPreviewData = content
						return .none

					case let .didLoadChartContent(.failure(error)):
						return state.errors
							.enqueue(.failedToLoadChart, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
							.map { .internal(.errors($0)) }

					case let .editor(.delegate(.didChangeConfiguration(configuration))):
						return refreshChart(
							withConfiguration: configuration?.withSubtitle(),
							state: &state
						)

					case .editor(.internal), .editor(.view), .editor(.binding),
							.errors(.delegate(.doNothing)), .errors(.internal), .errors(.view):
						return .none
					}

				case .delegate, .binding:
					return .none
				}
			}
		}
		.onChange(of: \.configuration) { _, configuration in
			Reduce<State, Action> { _, _ in
				return .run { @MainActor send in
					guard let configuration else { return }
					let imageRenderer = ImageRenderer(
						content: ShareableStatisticsImage(
							configuration: configuration
						)
						.frame(minWidth: 400)
						.environment(\.colorScheme, configuration.colorScheme)
					)
					imageRenderer.scale = configuration.displayScale

					guard let image = imageRenderer.uiImage else {
						return
					}

					guard !Task.isCancelled else { return }

					send(.delegate(.imageRendered(image)))
				}
				.cancellable(id: CancelID.imageRenderer, cancelInFlight: true)
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didLoadChartContent(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}

	private func refreshChart(
		withConfiguration configuration: StatisticsWidget.Configuration?,
		state: inout State
	) -> Effect<Action> {
		guard let configuration else {
			return .merge(
				.cancel(id: CancelID.loadingPreview),
				.send(.internal(.hideChart), animation: .easeInOut)
			)
		}

		return .run { send in
			await send(.internal(.didLoadChartContent(Result {
				try await statisticsWidgets.chart(configuration)
			})))
		}.cancellable(id: CancelID.loadingPreview, cancelInFlight: true)
	}
}

private extension StatisticsWidget.Configuration {
	func withSubtitle() -> StatisticsWidget.Configuration {
		.init(
			id: id,
			bowlerId: bowlerId,
			leagueId: leagueId,
			timeline: timeline,
			statistic: statistic,
			subtitle: "\(Strings.Sharing.Watermark.madeWithApproach) - \(Strings.Sharing.Watermark.tryApproach)"
		)
	}
}

// MARK: - View

@ViewAction(for: StatisticsWidgetSharing.self)
public struct StatisticsWidgetSharingView: View {
	@Bindable public var store: StoreOf<StatisticsWidgetSharing>

	@Environment(\.colorScheme) var colorScheme
	@Environment(\.displayScale) var displayScale

	public init(store: StoreOf<StatisticsWidgetSharing>) {
		self.store = store
	}

	public var body: some View {
		StatisticsWidgetConfigurationEditorView(
			store: store.scope(state: \.editor, action: \.internal.editor),
			footer: {
				colorSchemeSection
			}
		)
		.onAppear {
			send(.didUpdateColorScheme(colorScheme))
			send(.didUpdateDisplayScale(displayScale))
			send(.onAppear)
		}
		.onChange(of: displayScale) { send(.didUpdateDisplayScale(displayScale)) }
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
	}

	private var colorSchemeSection: some View {
		Section {
			AppearancePicker(selection: $store.preferredAppearance)
		}
	}
}
