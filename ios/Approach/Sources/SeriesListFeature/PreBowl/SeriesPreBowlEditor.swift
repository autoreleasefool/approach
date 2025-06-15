import AnalyticsServiceInterface
import ComposableArchitecture
import ErrorsFeature
import FeatureActionLibrary
import ModelsLibrary
import PickableModelsLibrary
import ResourcePickerLibrary
import SeriesRepositoryInterface
import StringsLibrary
import SwiftUI

@Reducer
public struct SeriesPreBowlEditor: Reducer, Sendable {
	@ObservableState
	public struct State: Equatable {
		public let league: League.ID
		public var series: Series.Summary?
		public var isSavingSeries: Bool = false

		public var originalDate: Date
		public var appliedDate: Date

		@Presents public var destination: Destination.State?
		public var errors: Errors<ErrorID>.State = .init()

		var isSaveEnabled: Bool {
			series != nil && !isSavingSeries
		}

		public init(league: League.ID) {
			self.league = league

			@Dependency(\.date) var date
			self.originalDate = date()
			self.appliedDate = date()
		}
	}

	public enum Action: FeatureAction, ViewAction, BindableAction {
		@CasePathable
		public enum View {
			case didTapSeries
			case didTapSaveButton
			case didTapCancelButton
		}
		@CasePathable
		public enum Delegate { case doNothing }
		@CasePathable
		public enum Internal {
			case didUpdateSeries(Result<Void, Error>)

			case destination(PresentationAction<Destination.Action>)
			case errors(Errors<ErrorID>.Action)
		}

		case view(View)
		case delegate(Delegate)
		case `internal`(Internal)
		case binding(BindingAction<State>)
	}

	@Reducer
	public struct Destination: Reducer, Sendable {
		public enum State: Equatable {
			case seriesPicker(ResourcePicker<Series.Summary, League.ID>.State)
		}

		public enum Action {
			case seriesPicker(ResourcePicker<Series.Summary, League.ID>.Action)
		}

		@Dependency(SeriesRepository.self) var series

		public var body: some ReducerOf<Self> {
			Scope(state: \.seriesPicker, action: \.seriesPicker) {
				ResourcePicker { league in series.unusedPreBowls(bowledIn: league) }
			}
		}
	}

	public enum ErrorID: Hashable, Sendable {
		case failedToUpdate
	}

	public init() {}

	@Dependency(\.date) var date
	@Dependency(\.dismiss) var dismiss
	@Dependency(SeriesRepository.self) var series

	public var body: some ReducerOf<Self> {
		BindingReducer()

		Scope(state: \.errors, action: \.internal.errors) {
			Errors()
		}

		Reduce<State, Action> { state, action in
			switch action {
			case let .view(viewAction):
				switch viewAction {
				case .didTapSeries:
					state.destination = .seriesPicker(.init(
						selected: Set([state.series?.id].compactMap { $0 }),
						query: state.league,
						limit: 1,
						showsCancelHeaderButton: false
					))
					return .none

				case .didTapSaveButton:
					guard let seriesId = state.series?.id else { return .none }
					state.isSavingSeries = true
					return .run { [appliedDate = state.appliedDate] send in
						await send(.internal(.didUpdateSeries(Result {
							try await series.usePreBowl(seriesId, appliedDate)
						})))
					}

				case .didTapCancelButton:
					return .run { _ in await dismiss() }
				}

			case let .internal(internalAction):
				switch internalAction {
				case .didUpdateSeries(.success):
					return .run { _ in await dismiss() }

				case let .didUpdateSeries(.failure(error)):
					return state.errors
						.enqueue(.failedToUpdate, thrownError: error, toastMessage: Strings.Error.Toast.failedToSave)
						.map { .internal(.errors($0)) }

				case .errors(.delegate(.doNothing)):
					return .none

				case let .destination(.presented(.seriesPicker(.delegate(delegateAction)))):
					switch delegateAction {
					case let .didChangeSelection(series):
						state.series = series.first
						state.originalDate = state.series?.date ?? date()
						return .none
					}

				case .destination(.dismiss),
						.destination(.presented(.seriesPicker(.internal))), .destination(.presented(.seriesPicker(.view))),
						.errors(.internal), .errors(.view):
					return .none
				}

			case .delegate, .binding:
				return .none
			}
		}
		.ifLet(\.$destination, action: \.internal.destination) {
			Destination()
		}

		ErrorHandlerReducer<State, Action> { _, action in
			switch action {
			case let .internal(.didUpdateSeries(.failure(error))):
				return error
			default:
				return nil
			}
		}
	}
}

@ViewAction(for: SeriesPreBowlEditor.self)
public struct SeriesPreBowlEditorView: View {
	@Bindable public var store: StoreOf<SeriesPreBowlEditor>

	public init(store: StoreOf<SeriesPreBowlEditor>) {
		self.store = store
	}

	public var body: some View {
		Form {
			Section {
				Text(Strings.Series.PreBowlEditor.Description.chooseToApply)

				Text(Strings.Series.PreBowlEditor.Description.affectsLeagueAverage)

				Text(Strings.Series.PreBowlEditor.Description.affectsBowlerAverage)
			}
			.listRowSeparator(.hidden)

			Section(Strings.Series.PreBowlEditor.Fields.series) {
				Button { send(.didTapSeries) } label: {
					LabeledContent(Strings.Series.title, value: store.series?.date.longFormat ?? Strings.none)
				}
				.buttonStyle(.navigation)
				.disabled(store.isSavingSeries)
			}

			if store.series != nil {
				Section {
					LabeledContent(
						Strings.Series.PreBowlEditor.Fields.originalDate,
						value: store.originalDate.longFormat
					)

					DatePicker(
						Strings.Series.PreBowlEditor.Fields.appliedDate,
						selection: $store.appliedDate,
						displayedComponents: [.date]
					)
					.datePickerStyle(.graphical)
					.disabled(store.isSavingSeries)
				}
			}
		}
		.navigationTitle(Strings.Series.PreBowlEditor.title)
		.toolbar {
			ToolbarItem(placement: .navigationBarTrailing) {
				Button(Strings.Action.save) { send(.didTapSaveButton) }
					.disabled(!store.isSaveEnabled)
			}

			ToolbarItem(placement: .navigationBarLeading) {
				Button(Strings.Action.cancel) { send(.didTapCancelButton) }
			}
		}
		.seriesPicker($store.scope(state: \.destination?.seriesPicker, action: \.internal.destination.seriesPicker))
		.errors(store: store.scope(state: \.errors, action: \.internal.errors))
	}
}

extension View {
	fileprivate func seriesPicker(_ store: Binding<StoreOf<ResourcePicker<Series.Summary, League.ID>>?>) -> some View {
		navigationDestination(item: store) { (store: StoreOf<ResourcePicker<Series.Summary, League.ID>>) in
			ResourcePickerView(store: store) { series in
				Text(series.date.longFormat)
			}
		}
	}
}
