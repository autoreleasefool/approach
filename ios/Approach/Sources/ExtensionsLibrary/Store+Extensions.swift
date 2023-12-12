import ComposableArchitecture

public typealias PresentationStoreOf<R: Reducer> = Store<PresentationState<R.State>, PresentationAction<R.Action>>
