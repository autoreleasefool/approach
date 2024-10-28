import AnalyticsServiceInterface
import ComposableArchitecture
import EquatablePackageLibrary
import ErrorsFeature
import FeatureActionLibrary
import Foundation
import LeaguesRepositoryInterface
import ModelsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import StatisticsChartsLibrary
import StatisticsDetailsFeature
import StatisticsLibrary
import StatisticsWidgetEditorFeature
import StatisticsWidgetsLibrary
import StatisticsWidgetsRepositoryInterface
import StringsLibrary
import TipsLibrary
import TipsServiceInterface

@Reducer
public struct StatisticsWidgetEditor: Reducer, Sendable {
	static let chartLoadingAnimationTime: TimeInterval = 0.5

	@ObservableState
	public struct State: Equatable {
		public let context: String
		public let priority: Int

		public var editor: StatisticsWidgetConfigurationEditor.State

		var isSaveable: Bool { editor.source != nil }

		public var isLoadingPreview = false
		public var widgetPreviewData: Statistics.ChartContent?

		public var isShowingTapThroughTip: Bool

		public var errors: Errors<ErrorID>.State = .init()

		@Presents public var destination: Destination.State?

		public init(context: String, priority: Int, source: StatisticsWidget.Source?) {
			self.context = context
			self.priority = priority
			self.editor = .init(source: source)

			@Dependency(TipsService.self) var tips
			self.isShowingTapThroughTip = tips.shouldShow(tipFor: .tapThroughStatisticTip)
		}
	}

	public enum Action: FeatureAction, ViewAction {
		@CasePathable
		public enum View {
			case onAppear
			case didTapSaveButton
			case didTapWidget
			case didTapDismissTapThroughTip
		}
		@CasePathable
		public enum Delegate {
			case didCreateConfiguration(StatisticsWidget.Configuration)
		}
		@CasePathable
		public enum Internal {
			case editor(StatisticsWidgetConfigurationEditor.Action)
			case destination(PresentationAction<Destination.Action>)
			case errors(Errors<ErrorID>.Action)

			case didStartLoadingPreview
			case didLoadChartContent(Result<Statistics.ChartContent, Error>)
			case didFinishSavingConfiguration(Result<StatisticsWidget.Configuration, Error>)
			case hideChart
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
	}

	@Reducer
	public struct Destination: Reducer, Sendable {
		public enum State: Equatable {
			case help(StatisticsWidgetHelp.State)
		}

		public enum Action {
			case help(StatisticsWidgetHelp.Action)
		}

		public var body: some ReducerOf<Self> {
			Scope(state: \.help, action: \.help) {
				StatisticsWidgetHelp()
			}
		}
	}

	public enum CancelID: Sendable {
		case loadingPreview
	}

	public enum ErrorID: Hashable {
		case failedToLoadChart
		case failedToSaveConfiguration
	}

	public init() {}

	@Dependency(\.continuousClock) var clock
	@Dependency(\.date) var date
	@Dependency(\.dismiss) var dismiss
	@Dependency(StatisticsWidgetsRepository.self) var statisticsWidgets
	@Dependency(TipsService.self) var tips
	@Dependency(\.uuid) var uuid

	public var body: some ReducerOf<Self> {
		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Scope(state: \.editor, action: \.internal.editor) {
			StatisticsWidgetConfigurationEditor()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .onAppear:
					return .none

				case .didTapWidget:
					switch state.widgetPreviewData {
					case .averaging, .counting, .percentage:
						return .none
					case .dataMissing, .chartUnavailable, .none:
						state.destination = .help(.init(missingStatistic: state.editor.configuration))
					}
					return .none

				case .didTapSaveButton:
					guard let configuration = state.editor.configuration,
								let widget = configuration.make(on: date(), context: state.context, priority: state.priority) else {
						return .none
					}
					return .run { send in
						await send(.internal(.didFinishSavingConfiguration(Result {
							try await self.statisticsWidgets.create(widget)
							return configuration
						})))
					}

				case .didTapDismissTapThroughTip:
					state.isShowingTapThroughTip = false
					return .run { _ in await tips.hide(tipFor: .tapThroughStatisticTip) }
				}

			case let .internal(internalAction):
				switch internalAction {
				case .hideChart:
					state.isLoadingPreview = false
					state.widgetPreviewData = nil
					return .none

				case .didStartLoadingPreview:
					state.isLoadingPreview = true
					state.widgetPreviewData = nil
					return .none

				case let .didLoadChartContent(.success(content)):
					state.widgetPreviewData = content
					return .none

				case let .didFinishSavingConfiguration(.success(configuration)):
					return .concatenate(
						.send(.delegate(.didCreateConfiguration(configuration))),
						.run { _ in await dismiss() }
					)

				case let .didLoadChartContent(.failure(error)):
					return state.errors
						.enqueue(.failedToLoadChart, thrownError: error, toastMessage: Strings.Error.Toast.failedToLoad)
						.map { .internal(.errors($0)) }

				case let .didFinishSavingConfiguration(.failure(error)):
					return state.errors
						.enqueue(.failedToSaveConfiguration, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				case .destination(.presented(.help(.delegate(.doNothing)))):
					return .none

				case .errors(.delegate(.doNothing)):
					return .none

				case let .editor(.delegate(.didChangeConfiguration(configuration))):
					return refreshChart(withConfiguration: configuration, state: &state)

				case .destination(.dismiss),
						.destination(.presented(.help(.internal))), .destination(.presented(.help(.view))),
						.editor(.view), .editor(.binding), .editor(.internal),
						.errors(.internal), .errors(.view):
					return .none
				}

			case .delegate:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination) {
			Destination()
		}

		BreadcrumbReducer<State, Action> { _, action in
			switch action {
			case .view(.onAppear): return .navigationBreadcrumb(type(of: self))
			default: return nil
			}
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didLoadChartContent(.failure(error))),
				let .internal(.didFinishSavingConfiguration(.failure(error))):
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
			return .send(.internal(.hideChart), animation: .easeInOut)
		}

		return .concatenate(
			.run { send in await send(.internal(.didStartLoadingPreview), animation: .easeInOut) },
			.run { send in
				let startTime = date()

				let result = await Result { try await statisticsWidgets.chart(configuration) }

				let timeSpent = date().timeIntervalSince(startTime)
				if timeSpent < Self.chartLoadingAnimationTime {
					try await clock.sleep(for: .milliseconds((Self.chartLoadingAnimationTime - timeSpent) * 1000))
				}

				await send(.internal(.didLoadChartContent(result)), animation: .easeInOut)
			}
		)
		.cancellable(id: CancelID.loadingPreview, cancelInFlight: true)
	}
}

extension Tip {
	static let tapThroughStatisticTip = Tip(
		id: "Widget.Builder.TapThrough",
		title: Strings.Widget.Builder.TapThrough.title,
		message: Strings.Widget.Builder.TapThrough.message
	)
}
